## Encounter mapping

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Meta |
| Not populated |  | implicitRules |
| Not populated |  | Language |
| `<Encounter><Text>` |  | Text |
| Not populated |  | Contained |
| Not populated |  | Extension (encounterTransport) |
| Not populated |  | Extension (outcomeOfAttendance) |
| Not populated |  | Extension (emergencyCareDischargeStatus) |
| Not populated |  | modifierExtension |
| Not populated |  | Identifier |
|  | HARDCODED: `"FINISHED"` | Status |
| Not populated |  | statusHistory |
| Not populated |  | Class |
| Not populated |  | ClassHistory |
| `DistributionEnvelope/header/handlingSpecification` |  | Type |
| Not populated |  | Priority |
| See ITK column of Patient mapping | Patient | Subject |
| Not populated |  | episodeOfCare |
| Not populated |  | IncomingReferral |
| See ITK column of Participant mapping | Participant | Participant |
| See ITK column of appointment mapping | Appointment | Appointment |
| `encompassingEncounter/effectiveTime/low` |  | Period.start |
| `encompassingEncounter/effectiveTime/high` |  | Period.end |
| Not populated |  | Length |
| Not populated |  | Reason |
| Not populated |  | Diagnosis |
| Not populated |  | Account |
| Not populated |  | Hospitalization |
| See ITK column of location mapping | Location<br />For location from `<healthCareFacility><location>`<br />location.status HARDCODED: `"COMPLETED"` | Location |
| See ITK column of service provider mapping | ServiceProvider | serviceProvider |
| Not populated |  | partOf |
| `encompassingEncounter/id` |  | identifier |

## RelatedPerson

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifier |
|  | HARDCODED: `true` | Active |
| See ITK column of Patient mapping | Patient | Pateint |
| When `<telecom use="EC">`<br /><br /><br />and/or `<ClinicalDocument><Informant><RelatedEntity><Code>` | HARDCODED:<br />Code – C<br />System – http://hl7.org/fhir/v2/0131<br />Display – Emergency Contact<br /><br />Code – code attribute<br />System – codeSystem attribute<br />Display – displayName attribute | Relationship |
| `<relatedEntity><RelatedPerson><name>` |  | Name |
| `<relatedEntity><telecom>` or `<patientRole><telecom>` when `<telecom use="EC">` |  | Telecom |
|  | HARDCODED: `"unknown"` | Gender |
| Not populated |  | birthDate |
| `<relatedEntity><Addr>` |  | Address |
| Not populated |  | Photo |
| `<relatedEntity><EffectiveTime>` |  | period |

## Condition

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Meta |
| Not populated |  | implicitRules |
| `<section><languageCode>` |  | Language |
| Not populated |  | Text |
| Not populated |  | Contained |
| Not populated |  | Extension |
| Not populated |  | ModifierExtension |
| Not populated |  | Identifier |
|  | HARDCODED: `"active"` | clinicalStatus |
|  | HARDCODED: `"unknown"` | verificationStatus |
| `<encounter><text>` |  | Category |
| Not populated |  | Severity |
| `<entry><contentId extension="COCD_TP146092GB01#ClinicalDiscriminator">`<br />For the correct entry:<br />`<entry><observation><value>` |  | Code |
| Not populated |  | bodySite |
| See ITK column of patient mapping | Patient | Subject |
| See ITK column of encounter mapping | Encounter | Context |
| Not populated |  | Onset |
| Not populated |  | Abatement |
| `<encounter><effectiveTime>` |  | assertedDate |
| Not populated |  | Asserter |
| Not populated |  | Stage |
| See ITK column of questionnaire response mapping | Questionnaire Response | Evidence |
| Not populated |  | note |

## HealthcareService

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Meta |
| Not populated |  | implicitRules |
| Not populated |  | Language |
| Not populated |  | Text |
| Not populated |  | Contained |
| Not populated |  | Extension |
| Not populated |  | ModifierExtension |
| Not populated |  | Identifier |
|  | HARDCODED: `true` | Active |
| See ITK column of referral request mapping | Organization | providedBy |
| Not populated |  | Category |
| Not populated |  | Type |
| Not populated |  | Specialty |
| See ITK column of location mapping | Location | Location |
| `<receivedOrganization><name>` |  | Name |
| Not populated |  | Comment |
| Not populated |  | extraDetails |
| Not populated |  | Photo |
| `<informationRecipient typeCode=”PRCP”><telecom>` |  | Telecom |
| Not populated |  | coverageArea |
| Not populated |  | serviceProvisionCode |
| Not populated |  | Eligibility |
| Not populated |  | EligibilityNote |
| Not populated |  | programName |
| Not populated |  | Characterisitic |
| Not populated |  | referralMethod |
| Not populated |  | appointmentRequired |
| Not populated |  | availableTime |
| Not populated |  | notAvailable |
| Not populated |  | availabilityExceptions |
| Not populated |  | Endpoints |

## Consent mapping

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | meta |
| Not populated |  | implicitRules |
| See ITK column of referral request mapping | Encounter.language | language |
| For sections with the following code<br />`<section><code code="887031000000108" codeSystem="2.16.840.1.113883.2.1.3.2.4.15"><section><text>` |  | text |
| Not populated |  | contained |
| Not populated |  | modifierExtension |
| Value = `<clinicalDocument><setId>` | Use = HARDCODED: USUAL | identifier |
|  | HARDCODED: ACTIVE | status |
| Not populated |  | category |
| See ITK column of referral request mapping | Encounter.subject | patient |
| See ITK column of referral request mapping | Encounter.period | period |
| Not populated |  | dateTime |
| Not populated |  | consentingParty |
| Not populated |  | Actor |
| `<authorization><consent><code>` |  | action |
| See ITK column of referral request mapping | Encounter.serviceProvider | organisation |
| `<section><id>` |  | source |
| Not populated |  | Policy |
|  | HARDCODED: http://hl7.org/fhir/ConsentPolicy/opt-out | policyRule |
| Not populated |  | SecurityLabel |
| Not populated |  | Purpose |
| For `<entries><contentId>` with root = "2.16.840.1.113883.2.1.3.2.4.18.16"<br />Extension = "COCD_TP146050GB01#PermissionToView"<br />`<entry><observation><effectivetime>` |  | dataPeriod |
| See ITK column of referral request mapping | Meaning = HARDCODED: RELATEDReference = encounter | data |
| Not populated |  | except |

## CarePlan

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | identifier |
| For section with code `<section><code code=”1052951000000105”> <section><languageCode>` |  | language |
| Not populated |  | definition |
| Not populated |  | basedOn |
| Not populated |  | replaces |
| Not populated |  | partOf |
|  | HARDCODED: COMPLETED | status |
|  | HARDCODED: PLAN | intent |
| Not populated |  | category |
| For section with code `<section><code code=”1052951000000105”>`<br /><br />`<section><title>` |  | title |
| For section with code`<section><code code=”1052951000000105”>`<br /><br />`<section><text>` |  | description |
| See ITK column of encounter mapping | Encounter.subject | subject |
| See ITK column of encounter mapping | Encounter | context |
| See ITK column of encounter mapping | Encounter.period | period |
| See ITK column of encounter mapping | Encounter.location.managingOrganization | author |
| Not populated |  | careTeam |
| See ITK column of condition mapping | Condition | addresses |
| Not populated |  | supportingInfo |
| Not populated |  | goal |
| Not populated |  | Activity |
| Not populated |  | Note |

## List

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Meta |
| Not populated |  | implicitRules |
| Not populated |  | Language |
| Not populated |  | Text |
| Not populated |  | Contained |
| Not populated |  | Extension |
| Not populated |  | modifierExtension |
| Value = `<clinicalDocument><setId root=”this value”>` | Use = HARDCODED: `"usual"` | Identifier |
|  | HARDCODED: `"current"` | Status |
| Not populated | HARDCODED: `"working"` | Mode |
| Not populated | HARDCODED: `"111 Report List"` | Title |
| Not populated | Hardcoded:<br />Code – 225390008<br />System – http://snomed.info/sct<br />display – Traige | Code |
| Not populated |  | intent |
| See ITK column of Patient mapping | Reference Patient | Subject |
| See ITK column of Encounter mapping | Reference Encounter | encounter |
| `<ClinicalDocument><effectiveTime>` |  | Date |
| See ITK column of Device mapping | Reference device | Source |
|  | Hardcoded:<br />Code – event-date<br />System – http://hl7.org/fhir/list-order<br />Display – Sorted by Event Date | orderedBy |
| Not populated |  | Note |
| See ITK column of<br />-        Condition<br />-        Questionnaire<br />-        QuestionnaireResponse<br />-        Observation<br />-        Organization<br />-        Practitioner<br />-        ReferralRequest<br />-        RelatedPersonMapping | Reference to any required resource:<br />-        Condition<br />-        Questionnaire<br />-        QuestionnaireRespons<br />-        Observation<br />-        Organization<br />-        Practitioner<br />-        ReferralRequest<br />-        RelatedPerson | Entry |
| Not populated |  | emptyReason. |

## Composition

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Meta |
| Not populated |  | implicitRules |
| Not populated |  | Language |
| Not populated |  | Text |
| Not populated |  | Contained |
| Not populated |  | Extension |
| Value = `<clinicalDocument><setId>` | Use = HARDCODED: `"usual"` | Identifier |
|  | HARDCODED: `"final"` | Status |
|  | Code = http://snomed.info/sct<br />Display = Report of clinical encounter (record artifact)<br />System = 371531000 | Type |
| Not populated |  | Class |
| See ITK column of Encounter mapping | Encounter.subject | Subject |
| See ITK column of Encounter mapping | Encounter | Encounter |
| `<ClinicalDocument><effectiveTime>` |  | Date |
| `<ClinicalDocument><author>` |  | Author |
|  | HARDCODED: `"111 Report"` | Title |
| `<clinicalDocument><confidentialityCode>` |  | Confidentiality |
| Not populated |  | Attester |
| Not populated |  | Custodian |
| Identifier value = `<ClinicalDocument><relatedDocument><parentDocument><Id root=”related document”><ClinicalDocument><setId root=”main document”>` |  | relatedTo |
| Title = `<component><structuredBody> <component><section><title>`<br />Text = `<component><structuredBody> <component><section><text>` |  | Event |
| See ITK column of Care Plan mapping<br />See ITK column of Referral Request mapping<br />See ITK column of Questionnaire Response mapping<br />`<component><structuredBody> <component><section><title>`<br />`<component><structuredBody> <component><section><text>` | Care planReferral RequestQuestionnaire Response | section |

## Referral Request

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifier |
| Not populated |  | Definition |
| Not populated |  | basedOn |
| Not populated |  | replaces |
| Not populated |  | groupIdentifier |
|  | HARDCODED: `"active"` | Status |
|  | HARDCODED: `"plan"` | Intent |
| Not populated |  | type |
|  | HARDCODED: `"routine"` | Priority |
| Not populated |  | serviceRequested |
| See ITK column of encounter mapping | Encounter.subject | Subject |
| See ITK column of encounter mapping | Encounter | context |
| `ClinicalDocument/effectiveTime` |  | Occurrence.start |
| `<author typeCode="AUT"><time>` |  | authoredOn |
|  | BehalfOf = Encounter.service<br />ProviderAgent = Reference Device | Requester |
| Not populated |  | Specialty |
| See ITK column of healthcare service mapping | Healthcare Service | Recipient |
| ClinicalDocument/participant with `typeCode=REFT`<br />See ITK column of Practitioner mapping | Practitioner |
| Not populated |  | reasonCode |
| See ITK column of condition mapping | Condition | reasonReference |
| Not populated |  | description |
| See ITK column of procedure request mapping | Procedure request | supportingInfo |
| Not populated |  | note |
| Not populated |  | relevantHistory |

## Device

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated | Current version | Version |
|  | HARDCODED: `"111 Adaptor"` | Model |

## Patient

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifier |
|  | True | Active |
| `<patientRole><patient>` |  | Name |
| `<patientRole><telecom>` |  | Telecom |
| `<patientRole><patient><administrativeGender>` |  | Gender |
| `<patientRole><patient><birthtime>` |  | birthDate |
| Not populated |  | Deceased |
| `<patientRole><addr>` |  | Address |
| `<patientRole><patient><languageCommunication>` |  | Language |
| `<patientRole><patient><maritalStatus>` |  | maritalStatus |
| Not populated |  | MultipleBirth |
| Not populated |  | Photo |
| `<patientRole><patient><guardian>` |  | Contact |
| Not populated |  | Animal |
| Not populated |  | Communication |
| `<patientRole><providerOrganization>` |  | generalPractitioner |
| Not populated |  | managingOrganization |
| Not populated |  | link |
| `<patientRole><patient><ethicGroup>`<br />`<patientRole><patient><religiousAffiliation>`<br />`<patientRole><patient><birthplace>` | HARDCODED: "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect-EthnicCategory-1"<br />HARDCODED: "https://fhir.hl7.org.uk/STU3/StructureDefinition/Extension-CareConnect"<br />HARDCODED: "http://hl7.org/fhir/StructureDefinition/birthPlace" | Extension |

## Questionnaire Response

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
| `<caseDetails><caseId>` |  | Identifier |
| No mapping |  | basedOn |
| No mapping |  | Parent |
| See ITK column of referral request mapping | Reference Questionnaire | Questionnaire |
|  | HARDCODED to `"completed"` | Status |
| See ITK column of Patient mapping | Reference patient | Subject |
| See ITK column of Encounter mapping | Reference encounter | Context |
| `<pathwayTriage><finish>` |  | Authored |
| No mapping |  | Author |
| No mapping |  | Source |
| See ITK column of Questionnaire Response – Item mapping below |  | item |

## Questionnaire Response – Item

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
| `<triageLine><question><questionId>` | If not available hardcoded to N/A | .linkId |
| No mapping |  | .definition |
| `<triageLine><question> <questionText>` |  | .text |
| No mapping |  | .subject |
| No mapping |  | Answer |
| No mapping |  | .value[x] |
| `<triageLine><question><answers> <answer selected=true><text>` |  | .value[x].answer |
| No mapping |  | .item |
| No mapping |  | item |

## Questionnaire

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | newRandomUUID | Id |
| No mapping |  | url |
| `<pathwaysCase><caseDetails><caseId>` |  | Identifier |
| `<pathwaysCase><caseReceiveEnd>` |  | Version |
| No mapping |  | Name |
| No mapping |  | Title |
|  | HARDCODED TO: `"active"` | Status |
|  | HARDCODED TO: `false` | Experimental |
| `<caseReceiveEnd>` |  | Date |
| `<pathwayTriage><user><name>` |  | Publisher |
| No mapping |  | Description |
| No mapping |  | Purpose |
| No mapping |  | approvalDate |
| `<caseReceiveEnd>` |  | lastReviewDate |
| No mapping |  | effectivePeriod |
| No mapping |  | useContext |
| `<caseDetails><address><country> <name>` |  | Jurisdiction |
| `<contactDetails><caller><name> <phone><number>` | HARDCODED:<br />System = `"phone"` | Contact |
| No mapping |  | Copyright |
| No mapping |  | Code |
| No mapping | Hardcoded to patient | subjectType |
| See ITK column of Questionnaire – Item Mapping below |  | item |

## Questionnaire - Item

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
| `<caseDetails><caseId>` |  | linkId |
| No mapping |  | Definition |
| No mapping |  | Code |
| `<triageLine><question> <triageLogicId><pathwayOrderNo>` |  | Prefix |
| `<triageLine><question> <questionText>` |  | Text |
| No mapping | HARDCODED TO: `"choice"` | Type |
| No mapping |  | enableWhen |
| No mapping |  | .Question |
| No mapping |  | .has Answer |
| No mapping |  | .answer |
|  | Hardcoded: `true` | .required |
|  | Hardcoded: `false` | .repeats |
| No mapping |  | .readOnly |
| No mapping |  | .maxLength |
| `<triageLine[x]>` |  | .options |
| `<triageLine><question><answers> <answer><text>`<br />&<br />`<triageLine><question><answers> <answer>` |  | .option.value |
| No mapping |  | .initial |
| No mapping |  | .item |

## Procedure Request

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New newRandomUuid generated | Id |
| Not populated |  | Identifier |
| Not populated |  | Definition |
| Not populated |  | basedOn |
| Not populated |  | Replaces |
| Not populated |  | Requisition |
|  | HARDCODED: `"active"` | Status |
|  | HARDCODED: `"plan"` | Intent |
|  | HARDCODED: `"routine"` | Priority |
|  | HARDCODED: `false` | doNotPerform |
| Not populated |  | Category |
| `<encompassingEncounter> <dischargeDispositionCode>` |  | Code |
| See ITK column of Encounter.subject mapping | Patient reference | Subject |
| Not populated |  | Context |
| `ClinicalDocument/effectiveTime` |  | Occurrence.start |
| Not populated |  | asNeeded |
| Not populated |  | authoredOn |
| Not populated |  | Requester |
| Not populated |  | performerType |
| Not populated |  | Performer |
| Not populated |  | reasonCode |
| See ITK column of referral request mapping | Referralrequest.reasonreference | reasonReference |
| Not populated |  | supportingInfo |
| Not populated |  | Specimen |
| Not populated |  | bodySite |
| Not populated |  | Note |
| Not populated |  | relevantHistory |

## Observation

| ITK |  | FHIR |
| --- | --- | --- |
| Not populated |  | Identifier |
| Not populated |  | basedOn |
|  | HARDCODED: `"final"` | Status |
| Not populated |  | Category |
|  | Hardcoded:<br />code = 33962009<br />Display = Presenting complaint<br />System = Patient.s Reported Condition | Code |
| See ITK column of patient mapping | Patient | Subject |
| See ITK column of encounter mapping | Encounter | Context |
| Not populated |  | Effective date |
| Not populated |  | Issued |
| Not populated |  | Performer |
| `/ClinicalDocument/component /structuredBody/component/ section/component[n]/section/text` |  | Value |
| Not populated |  | dataAbsentReason |
| Not populated |  | Interpretation |
| Not populated |  | Comment |
| Not populated |  | bodySite |
| Not populated |  | method |
| Not populated |  | specimen |
| Not populated |  | Device |
| Not populated |  | referenceRange |
| Not populated |  | Related |
| Not populated |  | Component |

## Location

### Location for appointment

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifer |
| Not populated |  | Status |
| Not populated |  | operationalStatus |
| `<participantRole><playingEntity><name>` |  | Name |
| Not populated |  | Alias |
| `<participantRole><playingEntity><desc>` |  | Description |
| Not populated |  | Mode |
| Not populated |  | Type |
| Not populated |  | Telecom |
| See ITK column of address mapping | address | Address |
| Not populated |  | PhysicalType |
| Not populated |  | Position |
| Not populated |  | managingOrganizaiton |
| Not populated |  | part of |
| Not populated |  | endpoint |

### Location for encounter

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifer |
|  | HARDCODED: `"active"` | Status |
| Not populated |  | operationalStatus |
| Not populated |  | Name |
| Not populated |  | Alias |
| Not populated |  | Description |
| Not populated |  | Mode |
| Not populated |  | Type |
| Not populated |  | Telecom |
| Not populated |  | Address |
| Not populated |  | PhysicalType |
| Not populated |  | Position |
| See ITK column of organization mapping | Organization | managingOrganizaiton |
| Not populated |  | part of |
| Not populated |  | endpoint |
| `<organizationpPartOf><effectiveTime>` |  | period |

### Location for encompassing encounter

Mapped from `<healthCareFacility><location>`

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifer |
| Not populated |  | Status |
| Not populated |  | operationalStatus |
| `<healthCareFacility><location><name>` |  | Name |
| Not populated |  | Alias |
| Not populated |  | Description |
| Not populated |  | Mode |
| Not populated |  | Type |
| Not populated |  | Telecom |
| See ITK column of address mapping | address | Address |
| Not populated |  | PhysicalType |
| Not populated |  | Position |
| Not populated |  | managingOrganizaiton |
| Not populated |  | part of |
| Not populated |  | endpoint |
| `<organizationpPartOf><effectiveTime>` |  | period |

### Location for healthcare service

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifer |
| Not populated |  | Status |
| Not populated |  | operationalStatus |
| Not populated |  | Name |
| Not populated |  | Alias |
| Not populated |  | Description |
| Not populated |  | Mode |
| Not populated |  | Type |
| `<intendedRecipient><telecom>` |  | Telecom |
| See ITK column of address mapping | address | Address |
| `<intendedRecipient><receivedOrganization>` |  | PhysicalType |
| Not populated |  | Position |
| Not populated |  | managingOrganizaiton |
| Not populated |  | part of |
| Not populated |  | endpoint |

## Appointment

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifier |
|  | HARDCODED: `"booked"` | Status |
| Not populated |  | serviceCategory |
| Not populated |  | serviceType |
| Not populated |  | Specialty |
| Not populated |  | appointmentType |
| `<encounter><code displayname>` |  | Reason |
| Not populated |  | Indication |
| Not populated |  | Priority |
| `<section><title>` |  | Description |
| Not populated |  | supportingInformation |
| Not populated |  | Start |
| Not populated |  | End |
| Not populated |  | minutesDuration |
| Not populated |  | Slot |
| Not populated |  | Created |
| `<section><text>` |  | Comment |
| Not populated |  | incomingReferral |
| See ITK column of patient/location mapping | Actor = patient/location<br />Required = HARDCODED: `"required"`<br />Status = HARDCODED: `"accepted"` | Participant |
| Not populated |  | requestedPeriod |

## Organization

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| `<organization><id extension>` |  | Identifier |
| Not populated |  | Active |
| In case of `typeCode="PRCP"`<br />Code:`<informationRecipient typeCode="PRCP">`<br />Display:`<intendedRecipient><receivedOrganization><name>`<br />Other cases:`<organization><standardIndustryClassCode displayname>` |  | Type |
| `<organization><name>` |  | Name |
| See ITK column of address mapping | Address | Alias |
| `<organization><telecom>` |  | Telecom |
| Not populated |  | Address |
| `<organization><organizationpartof><wholeorganization>` |  | part of |
| Not populated |  | Contact |
| Not populated |  | endpoint |

## Organization (service provider)

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifier |
|  | HARDCODED: `true` | Active |
| `<custodian><type>` |  | Type |
| `<custodianOrganization><name>` |  | Name |
| Not populated |  | Alias |
| `<custodianOrganization><telecom>` |  | Telecom |
| See ITK column of address mapping | address | Address |
| Not populated |  | part of |
| Not populated |  | Contact |
| Not populated |  | endpoint |

## Address

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
| `<addr use=H><addr use=HP><addr use=WP><addr use=TMP>` |  | Use |
| `<addr use=PHYS>` |  | Type |
| `<addr><desc>` |  | Text |
| Not populated |  | Line |
| `<addr><city>` |  | City |
| `<addr><precinct>` |  | District |
| `<addr><state>` |  | State |
| `<addr><postalcode>` |  | postalCode |
| `<addr><country>` |  | Country |
| `<addr><useableperiod>` |  | period |

## Practitioner

### Practitioner for participant

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifier |
|  | HARDCODED: `true` | Active |
| `<associatedEntity><associatedPerson><name>` |  | Name |
| `<associatedEntity><telecom>` |  | Telecom |
| See ITK column of address mapping | address | Address |
| Not populated |  | Gender |
| Not populated |  | birthDate |
| Not populated |  | Photo |
| Not populated |  | Qualification |
| Not populated |  | Coomunication |

### Practitioner for informant & data enterer

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifier |
|  | HARDCODED: true | Active |
| `<assignedEntity><assignedPerson><name>` |  | Name |
| `<assignedEntity><telecom>` |  | Telecom |
| See ITK column of address mapping | address | Address |
| Not populated |  | Gender |
| Not populated |  | birthDate |
| Not populated |  | Photo |
| Not populated |  | Qualification |
| Not populated |  | Coomunication |

### Practitioner for author

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
|  | New uuid generated | Id |
| Not populated |  | Identifier |
|  | HARDCODED: `true` | Active |
| `<assignedEntity><assignedAuhor><name>` |  | Name |
| `<assignedEntity><telecom>` |  | Telecom |
| See ITK column of address mapping | address | Address |
| Not populated |  | Gender |
| Not populated |  | birthDate |
| Not populated |  | Photo |
| Not populated |  | Qualification |
| Not populated |  | Coomunication |

## Participant

### Practitioner

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
| See ITK column of practitioner mapping | Practitioner | individual |
| PPRF – author; ATND – Responsible Party |  | Type.code |
| Author/Responsible Party |  | Type.display |
| http://hl7.org/fhir/ValueSet/encounter-participant-type (for Author and Responsible Party only) |  | Type.system |

### Practitioner

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
| See ITK column of related person mapping | Related person or | individual |
| `<ClinicalDocument><Informant>` with typeCode INF | HARDCODED: `"Informant"` | Type.text |

### Data enterer

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
| See ITK column of practitioner mapping | Practitioner | individual |

### Informant

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
| See ITK column of practitioner mapping | Practitioner | individual |

### Author

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
| See ITK column of practitioner mapping | Practitioner | individual |
|  |  |  |

## Practitioner Role (ResponsibleParty)

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
| `<responsibleParty><assignedEntity>` | PractitionerRole | practitioner |
| `<responsibleParty><assignedEntity><code>` |  | code |
| `<responsibleParty><assignedEntity><representedOrganization>` |  | organization |

## MessageHeader

| ITK Mapping Element | FHIR Objects 3.0.2 | FHIR Mapping Elements 3.0.2 |
| --- | --- | --- |
| `<itk:header><itk:addresslist><itk:address uri="ODS_CODE" />`<br />`<itk:header><itk:addresslist><itk:address type="2.16.840.1.113883.2.1.3.2.4.18.44" uri="DOS_ID" />` |  | destination.ednpoint (two values concatenated ODS_CODE:DOSServiceID:DOS_ID) |
| `<itk:header><a:MessageID>` |  | id |
| `<itk:header><itk:handlingSpecification><itk:spec value>` |  | Event |
| Code: `<itk:header><itk:handlingSpecification><itk:spec value>`<br />System: `<itk:header><itk:handlingSpecification><itk:spec key>` |  | Reason |
| `<itk:header><itk:handlingSpecification><itk:senderAddress>` |  | Destination |
| `<ClinicalDocument><effectiveTime>` |  | timestamp |