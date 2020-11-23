# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
## [0.4.0] - 2020-11-23
- TLS Mutual authentication
- PractitionerRole resource is mapped to FHIR using following mapping:
    FHIR | ITK
    ---- | ---
    PractitionerRole/code | ClinicalDocument/componentOf/encompassingEncounter/responsibleParty/assignedEntity/code
    PractitionerRole/Practitioner | ClinicalDocument/componentOf/encompassingEncounter/responsibleParty/assignedEntity
    PractitionerRole/Organization | ClinicalDocument/componentOf/encompassingEncounter/responsibleParty/representedOrganization
## [0.3.0] - 2020-11-18
- Populate interaction type in FHIR message
- Author Organisation resource is mapped to FHIR using following mapping:
    FHIR | ITK
    ---- | ---
    Organisation | author/representedOrganization
- Author Organisation is linked to Composition → Author
- Author PractitionerRole resource is mapped to FHIR using following mapping:
    FHIR | ITK
    ---- | ---
    PractitionerRole.code.system | author/assignedAuthor/code/codeSystem
    PractitionerRole.code.code | author/assignedAuthor/code/code
    PractitionerRole.code.display | author/assignedAuthor/code/displayName
- Author PractitionerRole is linked to Composition → Author
- ITK address list is populated in FHIR MessageHeader using following mapping:
    FHIR | ITK
    ---- | ---
    MessageHeader/destination/endpoint/ | DistributionEnvelope/header/addresslist

## [0.2.0] - 2020-10-26
### Added
- ITK/SOAP Validation

### Changed
- ITK -> FHIR mapping updates

## [0.1.0] - 2020-08-06
### Added
- First release of 111 PEM Adaptor
- /report endpoint for ITK SOAP -> FHIR mapping
