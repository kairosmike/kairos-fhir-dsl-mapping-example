package projects.dktk_with_jpa_navigation


import org.hl7.fhir.r4.model.Observation

import static de.kairos.fhir.centraxx.metamodel.AbstractCode.CODE
import static de.kairos.fhir.centraxx.metamodel.RootEntities.surgery

/**
 * Represented by a CXX Surgery
 * @author Mike Wähnert
 * @since CXX.v.3.17.0.7
 */
observation {
  id = "Observation/GesamtbeurteilungResidualstatus-" + context.source[surgery().id()]

  meta {
    profile "http://dktk.dkfz.de/fhir/StructureDefinition/onco-core-Observation-GesamtbeurteilungResidualstatus"
  }

  status = Observation.ObservationStatus.UNKNOWN

  category {
    coding {
      system = "http://hl7.org/fhir/observation-category"
      code = "procedure"
    }
  }
  code {
    coding {
      system = "http://loinc.org"
      code = "81169-5"
    }
  }

  subject {
    reference = "Patient/" + context.source[surgery().patientContainer().id()]
  }

  if (context.source[surgery().episode()]) {
    encounter {
      reference = "Encounter/" + context.source[surgery().episode().id()]
    }
  }

  effectiveDateTime {
    date = normalizeDate(context.source["date"] as String)
  }
  //TODO: date? Method für surgery nicht vorhanden.

  valueCodeableConcept {
    coding {
      system = "http://dktk.dkfz.de/fhir/onco/core/CodeSystem/GesamtbeurteilungResidualstatusCS"
      code = context.source[surgery().rClassificationDict()]?.getAt(CODE)?.toString()?.toUpperCase()
    }
  }
}

/**
 * removes milli seconds and time zone.
 * @param dateTimeString the date time string
 * @return the result might be something like "1989-01-15T00:00:00"
 */
static String normalizeDate(final String dateTimeString) {
  return dateTimeString != null ? dateTimeString.substring(0, 19) : null
}
