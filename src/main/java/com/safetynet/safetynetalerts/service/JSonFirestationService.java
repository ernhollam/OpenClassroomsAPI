package com.safetynet.safetynetalerts.service;

import com.safetynet.safetynetalerts.exceptions.ResourceNotFoundException;
import com.safetynet.safetynetalerts.model.Firestation;
import com.safetynet.safetynetalerts.model.Person;
import com.safetynet.safetynetalerts.model.viewmodel.FirePersonViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.FirestationPersonViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.FirestationViewModel;
import com.safetynet.safetynetalerts.model.viewmodel.FloodViewModel;
import com.safetynet.safetynetalerts.repository.FirestationRepository;
import com.safetynet.safetynetalerts.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class JSonFirestationService implements FirestationService {

    /**
     * Instance of FirestationRepository.
     */
    @Autowired
    private FirestationRepository firestationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private JSonPersonService jSonPersonService;

    /**
     * Saves new firestation.
     *
     * @param firestation
     *         Firestation to save
     *
     * @return Firestation
     */
    @Override
    public Firestation saveFirestation(final Firestation firestation) throws Exception {
        String                address   = firestation.getAddress();
        Optional<Firestation> duplicate = firestationRepository.findByAddress(address);
        if (duplicate.isPresent()) {
            firestationRepository.deleteByAddress(address);
        }
        return firestationRepository.save(firestation);
    }

    /**
     * Returns a list of fire stations with given station number.
     *
     * @param stationNumber
     *         ID of firestation to get
     *
     * @return Firestation a firestation if not empty
     */
    @Override
    public List<Firestation> getFirestation(final int stationNumber) {
        return firestationRepository.findByStationNumber(stationNumber);
    }

    /**
     * Returns the list of all fire stations.
     *
     * @return an iterable of Firestations
     */
    @Override
    public List<Firestation> getFirestations() {
        return firestationRepository.findAll();
    }


    /**
     * Updates firestation with given station number.
     *
     * @param firestation
     *         Firestation to update
     */
    @Override
    public Firestation updateFirestation(final Firestation firestation) throws Exception {
        String address = firestation.getAddress();
        Optional<Firestation> firestationInDataSource =
                firestationRepository.findByAddress(address);
        if (firestationInDataSource.isEmpty()) {
            String errorMessage = "There is no fire station at the following address: " + address + ".";
            log.error(errorMessage);
            throw new ResourceNotFoundException(errorMessage);
        } else {
            log.info("Updated firestation {}.", firestation);
            return saveFirestation(firestation);
        }
    }

    /**
     * Deletes firestation with given station number.
     *
     * @param address
     *         ID of firestation to delete
     */
    @Override
    public void deleteFirestation(final String address) throws Exception {
        firestationRepository.deleteByAddress(address);
    }


    /**
     * Returns people at addresses covered by a fire station.
     *
     * @param stationNumber
     *         Station number
     *
     * @return a list of people covered by a fire station
     */
    @Override
    public FirestationViewModel getPeopleCoveredByStation(int stationNumber) {
        FirestationViewModel             result            = new FirestationViewModel();
        List<FirestationPersonViewModel> firestationPeople = new ArrayList<>();

        List<Firestation> firestationsCovered    = firestationRepository.findByStationNumber(stationNumber);
        List<Person>      peopleCoveredByStation = new ArrayList<>();

        // browse list of addresses covered by fire station
        for (Firestation firestation : firestationsCovered) {
            String       firestationAddress    = firestation.getAddress();
            List<Person> peopleLivingAtAddress = personRepository.findByAddress(firestationAddress);
            // browse people living at address covered by fire station and add them all to final list
            for (Person person : peopleLivingAtAddress) {
                // search for duplicates in already counted persons
                if (!peopleCoveredByStation.contains(person)) {
                    peopleCoveredByStation.add(person);
                    // transform Person object to FirestationPerson
                    firestationPeople.add(new FirestationPersonViewModel(person.getFirstName(),
                                                                         person.getLastName(),
                                                                         person.getAddress(),
                                                                         person.getPhone()));
                }
            }
        }
        result.setPeople(firestationPeople);
        // get number of adults and children
        Map<String, Integer> nbFamilyMembers = jSonPersonService.getNbChildrenAndNbAdults(peopleCoveredByStation);
        result.setNbAdults(nbFamilyMembers.get("nb_adults"));
        result.setNbChildren(nbFamilyMembers.get("nb_children"));

        log.debug("The following people are covered by station number {}:\n{}", stationNumber, firestationPeople);

        return result;
    }

    /**
     * Returns a list of all phone numbers of people covered by a fire station.
     *
     * @param stationNumber
     *         Station number for which the list of covered people's phone number is wanted.
     *
     * @return List of phone numbers.
     */
    @Override
    public Set<String> getPhoneAlert(int stationNumber) {
        Set<String>                      phoneNumbers  = new HashSet<>();
        List<FirestationPersonViewModel> coveredPeople = getPeopleCoveredByStation(stationNumber).getPeople();
        for (FirestationPersonViewModel firestationPerson : coveredPeople) {
            phoneNumbers.add(firestationPerson.getPhone());
        }
        log.debug("List of phone numbers covered by station number {}:\n{}", stationNumber, phoneNumbers);
        return phoneNumbers;
    }

    /**
     * Returns list of households covered by a station. The list contains the name of each person living in the
     * household, including medical record.
     *
     * @param stations
     *         Station number for which list of covered households is wanted.
     *
     * @return List of households and their inhabitants.
     */
    @Override
    public FloodViewModel getCoveredHouseholds(List<Integer> stations) {
        // find addresses covered by fire station
        List<Firestation> firestations = new ArrayList<>();
        for (Integer station : stations) {
            List<Firestation> firestationsCoveredByStation = firestationRepository.findByStationNumber(station);
            firestations.addAll(firestationsCoveredByStation);
        }
        List<String> households = firestations.stream()
                                              .map(Firestation :: getAddress)
                                              .distinct()
                                              .collect(Collectors.toList());
        // browse addresses and collect people living at these addresses as FirePersonViewModel
        Map<String, List<FirePersonViewModel>> coveredHouseholds = new HashMap<>();
        for (String household : households) {
            List<FirePersonViewModel> firePeopleInHousehold = new ArrayList<>();
            List<Person>              peopleInHousehold     = personRepository.findByAddress(household);
            for (Person person : peopleInHousehold) {
                firePeopleInHousehold.add(jSonPersonService.personToFirePerson(person));
            }
            coveredHouseholds.put(household, firePeopleInHousehold);
        }

        log.debug("The following households and their members are covered by station number {}:\n{}", stationNumber,
                  coveredHouseholds);

        return new FloodViewModel(coveredHouseholds);
    }
}
