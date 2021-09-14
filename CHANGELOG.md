# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.9.0] - 2021-09-14
- Replace dom4j with standard Java xpath library
- Fixed issue with Date/Time parsing
- When handling repeat caller case, mapping only one `ClinicalDocument` with the newest date from `EffectiveTime` property
- Mapping `IntendedRecipient.ReceivedOrganization` to FHIR `Organization` resource and referencing it in
  `HealthcareService.Location.ManagingOrganization`and in `HealthcareService.ProvidedBy` properties
- Adding `Device` resource and referencing it in `ReferralRequest.Requester.Agent` and `List.Source` instead of hardcoded 'Device/1' value.
  `Device` resource is populated with:
    FHIR | value
    ---- | ---
    model | 111 Adaptor
    version | <current_version>
- Possibility to override supported ODS codes and DOS IDs set in yaml and retrieve them from endpoint configured in `PEM111_ITK_EXTERNAL_CONFIGURATION_URL` env var.
  Poll intervals can be set in `PEM111_ITK_FETCH_INTERVAL_MIN` env var.
## [0.8.0] - 2021-08-27
- possibility to switch off SOAP Header validation using env var - `PEM111_SOAP_VALIDATION_ENABLED`
- Fixed issues with Date/Time parsing
- Mapping `ClinicalDocument.effectiveTime` [ITK] to `MessageHeader.timestamp` [FHIR]
- When mapping `EncompassingEncounter.ResponsibleParty` to `Encounter.Participant` then `type` is populated with:
    FHIR | value
    ---- | ---
    Type.Coding.Code | ATND
    Type.Coding.Display | Responsible Party
    Type.Coding.System | http://hl7.org/fhir/ValueSet/encounter-participant-type
- When mapping `ClinicalDocument.Author` to `Encounter.Participant` then `type` is populated with:
    FHIR | value
    ---- | ---
    Type.Coding.Code | PPRF
    Type.Coding.Display | Author
    Type.Coding.System | http://hl7.org/fhir/ValueSet/encounter-participant-type
- Mapping `ITK ClinicalDocument.Informant.RelatedEntity.Code` to `RelatedPerson.Relationship`:
    ITK | FHIR
    ---- | ---
    codeSystem | relationship.coding.system
    code | relationship.coding.code
    displayName | relationship.coding.display
- Creating `RelatedPerson` resource where `relationship` is coded as emergency contact when mapping an entity with Telecom emergency use.    
- Mapping `EncompassingEncounter.Location.HealthCareFacility.Location` [ITK] to `Encounter.location` [FHIR]
- Including `typeCode` when mapping `informationRecipient`to `Organization`
- Mapping `informationRecipient` only when `typeCode=PRCP`
- Mapping `ClinicalDocument.participant` with `typeCode=REFT` to `ReferralRequest.recipient` instead of `Encounter.participant`
- Possibility to reject incorrectly addressed messages by configuring list of supported ODS codes and DOS IDs

## [0.7.0] - 2021-08-11
- DOS Service ID suffixed to ODS Code and mapped to FHIR MessageHeader/destination/endpoint according to pattern:
  ODS_code_value:DOSServiceID:DOS_Service_ID_value, where:
    name | ITK field
    ---- | ---
    ODS_code_value | Header/AddressList/Address field with only uri attribute/Uri attribute
    DOS_Service_ID_value | Header/AddressList/Address field with both uri and type attributes/Uri attribute
- AdministrativeGenderCode code attribute mapped to FHIR Patient/gender
  based on guidance from 'Person Stated Gender Code NHS Data Model and Dictionary':
    FHIR | ITK
    ---- | ---
    AdministrativeGenderCode code | FHIR Enumerations.AdministrativeGender
    1 | MALE
    2 | FEMALE
    9 | OTHER
    any other code | UNKNOWN
- All present EncompassingEncounter/Id mapped to FHIR using following mapping:
    FHIR | ITK
    ---- | ---
    Identifier[*].system | EncompassingEncounter/Id root attribute
    Identifier[*].value | EncompassingEncounter/Id extension attribute
- All FHIR resources added id property, set to value from fullUrl property without 'urn:uuid:' prefix.
- Header/A:MessageID mapped to FHIR MessageHeader/fullUrl prefixed with 'urn:uuid:'
  and to FHIR MessageHeader/id
- Addr use="PHYS" attribute mapped to FHIR Address/type set to 'physical'
- When HandlingSpecification is primary interaction it's mapped to FHIR using following mapping:
    FHIR | value
    ---- | ---
    MessageHeader/event/code | referral-1
    MessageHeader/event/display | Referral
    Encounter.text | 111 Encounter Referral
- When HandlingSpecification is copy interaction it's mapped to FHIR using following mapping:
    FHIR | value
    ---- | ---
    MessageHeader/event/code | discharge-details-1
    MessageHeader/event/display | Discharge Details
    Encounter.text | 111 Encounter Copy for Information
## [0.6.0] - 2021-06-25
- Audit identity validation
- POCD precompiled classes added to repo
## [0.5.0] - 2020-12-01
- CDA Section nesting preserved in FHIR
- ClinicalDocument versionNumber mapped to FHIR Bundle.identifier
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
