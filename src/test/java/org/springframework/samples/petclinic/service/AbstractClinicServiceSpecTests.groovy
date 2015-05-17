package org.springframework.samples.petclinic.service

import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.samples.petclinic.model.*
import org.springframework.samples.petclinic.util.EntityUtils
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

import static org.assertj.core.api.Assertions.assertThat

abstract class AbstractClinicServiceSpecTests extends Specification {

    @Autowired
    protected ClinicService clinicService


    def "should findOwners by lastName"() {
        expect:
        clinicService.findOwnerByLastName(filter).size() == size

        where:
        filter   || size
        'Davis'  || 2
        'Daviss' || 0
    }

    def "should findOwners by lastName elaborated"() {
        expect:
        clinicService.findOwnerByLastName(filter).lastName == lastNames

        where:
        filter   || lastNames
        'Davis'  || ['Davis'] * 2
        'Daviss' || []
    }

    void "should find single owner with pet"() {
        def owner = clinicService.findOwnerById(1)

        expect:
        owner.lastName ==~ /Franklin.*/
        owner.pets.name == ['Leo']
    }


    //TODO: convert to spec

    @Transactional
    void shouldInsertOwner() {
        Collection<Owner> owners = this.clinicService.findOwnerByLastName("Schultz")
        int found = owners.size()

        Owner owner = new Owner()
        owner.setFirstName("Sam")
        owner.setLastName("Schultz")
        owner.setAddress("4, Evans Street")
        owner.setCity("Wollongong")
        owner.setTelephone("4444444444")
        this.clinicService.saveOwner(owner)
        assertThat(owner.getId().longValue()).isNotEqualTo(0)

        owners = this.clinicService.findOwnerByLastName("Schultz")
        assertThat(owners.size()).isEqualTo(found + 1)
    }

    @Transactional
    void shouldUpdateOwner() {
        Owner owner = this.clinicService.findOwnerById(1)
        String oldLastName = owner.getLastName()
        String newLastName = oldLastName + "X"

        owner.setLastName(newLastName)
        this.clinicService.saveOwner(owner)

        // retrieving new name from database
        owner = this.clinicService.findOwnerById(1)
        assertThat(owner.getLastName()).isEqualTo(newLastName)
    }

    void shouldFindPetWithCorrectId() {
        Pet pet7 = this.clinicService.findPetById(7)
        assertThat(pet7.getName()).startsWith("Samantha")
        assertThat(pet7.getOwner().getFirstName()).isEqualTo("Jean")

    }

    void shouldFindAllPetTypes() {
        Collection<PetType> petTypes = this.clinicService.findPetTypes()

        PetType petType1 = EntityUtils.getById(petTypes, PetType.class, 1)
        assertThat(petType1.getName()).isEqualTo("cat")
        PetType petType4 = EntityUtils.getById(petTypes, PetType.class, 4)
        assertThat(petType4.getName()).isEqualTo("snake")
    }


    @Transactional
    void shouldInsertPetIntoDatabaseAndGenerateId() {
        Owner owner6 = this.clinicService.findOwnerById(6)
        int found = owner6.getPets().size()

        Pet pet = new Pet()
        pet.setName("bowser")
        Collection<PetType> types = this.clinicService.findPetTypes()
        pet.setType(EntityUtils.getById(types, PetType.class, 2))
        pet.setBirthDate(new DateTime())
        owner6.addPet(pet)
        assertThat(owner6.getPets().size()).isEqualTo(found + 1)

        this.clinicService.savePet(pet)
        this.clinicService.saveOwner(owner6)

        owner6 = this.clinicService.findOwnerById(6)
        assertThat(owner6.getPets().size()).isEqualTo(found + 1)
        // checks that id has been generated
        assertThat(pet.getId()).isNotNull()
    }


    @Transactional
    void sholdUpdatePetName() throws Exception {
        Pet pet7 = this.clinicService.findPetById(7)
        String oldName = pet7.getName()

        String newName = oldName + "X"
        pet7.setName(newName)
        this.clinicService.savePet(pet7)

        pet7 = this.clinicService.findPetById(7)
        assertThat(pet7.getName()).isEqualTo(newName)
    }


    void shouldFindVets() {
        Collection<Vet> vets = this.clinicService.findVets()

        Vet vet = EntityUtils.getById(vets, Vet.class, 3)
        assertThat(vet.getLastName()).isEqualTo("Douglas")
        assertThat(vet.getNrOfSpecialties()).isEqualTo(2)
        assertThat(vet.getSpecialties().get(0).getName()).isEqualTo("dentistry")
        assertThat(vet.getSpecialties().get(1).getName()).isEqualTo("surgery")
    }


    @Transactional
    void shouldAddNewVisitForPet() {
        Pet pet7 = this.clinicService.findPetById(7)
        int found = pet7.getVisits().size()
        Visit visit = new Visit()
        pet7.addVisit(visit)
        visit.setDescription("test")
        this.clinicService.saveVisit(visit)
        this.clinicService.savePet(pet7)

        pet7 = this.clinicService.findPetById(7)
        assertThat(pet7.getVisits().size()).isEqualTo(found + 1)
        assertThat(visit.getId()).isNotNull()
    }


}
