package org.springframework.samples.petclinic.model

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import spock.lang.Specification

import javax.validation.Validator

public class ValidatorSpecTests extends Specification {

    def "should not validate when firstName is Empty"() {
        given:
        LocaleContextHolder.setLocale(Locale.ENGLISH)
        Person person = new Person(firstName: '', lastName: 'smith')
        def validator = createValidator()
        def constraintViolations = validator.validate(person)

        expect:
        constraintViolations.size() == 1
        constraintViolations[0].propertyPath.toString() == "firstName"
        constraintViolations[0].message == "may not be empty"
    }

    private Validator createValidator() {
        def localValidatorFactoryBean = new LocalValidatorFactoryBean()
        localValidatorFactoryBean.afterPropertiesSet()
        return localValidatorFactoryBean
    }

}
