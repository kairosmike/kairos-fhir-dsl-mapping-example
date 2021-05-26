package projects.gecco


import de.kairos.fhir.centraxx.metamodel.CatalogEntry
import de.kairos.fhir.centraxx.metamodel.LaborFindingLaborValue
import de.kairos.fhir.centraxx.metamodel.LaborValue
import de.kairos.fhir.centraxx.metamodel.enums.LaborMappingType
import org.hl7.fhir.r4.model.Observation

import static de.kairos.fhir.centraxx.metamodel.RootEntities.laborMapping

/**
 * Represented by a CXX LaborMapping
 * @author Lukas Reinert
 * @since CXX.v.3.18.0*
 * Maps the following profile:
 *  - SARS-CoV-2 (COVID-19) IgA Ab [Presence] in Serum or Plasma by Immunoassay
 */


observation {

  if (context.source[laborMapping().laborFinding().laborMethod().code()] != "COVID_IMMUNOASSAY_PROFILE_CODE") {
    return // no export
  }

  final def parameterCode = context.source[laborMapping().laborFinding().laborFindingLaborValues()].find {
    "COVID_IGA_AB_PRESENCE_IMMUNOASSAY_CODE" == it[LaborFindingLaborValue.LABOR_VALUE]?.getAt(LaborValue.CODE)
  }
  if (parameterCode) {
    id = "AbIgAPresence/" + context.source[laborMapping().laborFinding().id()]
    meta {
      profile "https://www.netzwerk-universitaetsmedizin.de/fhir/StructureDefinition/sars-cov-2-iga-ser-pl-ql-ia"
    }
    code {
      coding {
        system = "http://loinc.org"
        code = "94562-6"
      }
    }

    status = Observation.ObservationStatus.UNKNOWN

    category {
      coding {
        system = "http://loinc.org"
        code = "26436-6"
      }
      coding {
        system = "http://terminology.hl7.org/CodeSystem/observation-category"
        code = "laboratory"
      }
    }

    subject {
      reference = "Patient/" + context.source[laborMapping().relatedPatient().id()]
    }
    final def episodeID = context.source[laborMapping().episode().id()]
    if (episodeID) {
      encounter {
        reference = "Episode/" + episodeID
      }
    }

    effectiveDateTime {
      date = context.source[laborMapping().laborFinding().findingDate().date()]
    }

    valueCodeableConcept {
      parameterCode[LaborFindingLaborValue.CATALOG_ENTRY_VALUE].each { final entry ->
        coding {
          system = "http://snomed.info/sct"
          code = entry[CatalogEntry.CODE] as String
        }
      }
    }

    //If the measurement profile contains a measurement parameter with code "ANNOTATION_CODE" and type "String"
    final def antibodyAnnotation = context.source[laborMapping().laborFinding().laborFindingLaborValues()].find {
      "ANNOTATION_CODE" == it[LaborFindingLaborValue.LABOR_VALUE]?.getAt(LaborValue.CODE)
    }
    if (antibodyAnnotation) {
      note {
        text = antibodyAnnotation[LaborFindingLaborValue.STRING_VALUE]
      }
    }

    if (context.source[laborMapping().mappingType()] == LaborMappingType.SAMPLELABORMAPPING as String) {
      specimen {
        reference = "Specimen/" + context.source[laborMapping().relatedOid()]
      }
    }
  }
}