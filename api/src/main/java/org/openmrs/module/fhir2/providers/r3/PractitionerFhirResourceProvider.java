/*
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.fhir2.providers.r3;

import javax.validation.constraints.NotNull;

import java.util.List;

import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.Setter;
import org.hl7.fhir.convertors.conv30_40.Bundle30_40;
import org.hl7.fhir.convertors.conv30_40.Practitioner30_40;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir2.api.FhirPractitionerService;
import org.openmrs.module.fhir2.providers.util.FhirProviderUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("practitionerFhirR3ResourceProvider")
@Qualifier("fhirR3Resources")
@Setter(AccessLevel.PACKAGE)
public class PractitionerFhirResourceProvider implements IResourceProvider {
	
	@Autowired
	private FhirPractitionerService practitionerService;
	
	@Override
	public Class<? extends IBaseResource> getResourceType() {
		return Practitioner.class;
	}
	
	@Read
	@SuppressWarnings("unused")
	public Practitioner getPractitionerById(@IdParam @NotNull IdType id) {
		org.hl7.fhir.r4.model.Practitioner practitioner = practitionerService.getPractitionerByUuid(id.getIdPart());
		if (practitioner == null) {
			throw new ResourceNotFoundException("Could not find practitioner with Id " + id.getIdPart());
		}
		
		return Practitioner30_40.convertPractitioner(practitioner);
	}
	
	@History
	@SuppressWarnings("unused")
	public List<Resource> getPractitionerHistoryById(@IdParam @NotNull IdType id) {
		org.hl7.fhir.r4.model.Practitioner practitioner = practitionerService.getPractitionerByUuid(id.getIdPart());
		if (practitioner == null) {
			throw new ResourceNotFoundException("Could not find practitioner with Id " + id.getIdPart());
		}
		return Practitioner30_40.convertPractitioner(practitioner).getContained();
	}
	
	@Search
	@SuppressWarnings("unused")
	public Bundle findPractitionersByName(@RequiredParam(name = Practitioner.SP_NAME) @NotNull String name) {
		return Bundle30_40.convertBundle(
		    FhirProviderUtils.convertSearchResultsToBundle(practitionerService.findPractitionerByName(name)));
	}
	
	@Search
	@SuppressWarnings("unused")
	public Bundle findPractitionersByIdentifier(
	        @RequiredParam(name = Practitioner.SP_IDENTIFIER) @NotNull String identifier) {
		return Bundle30_40.convertBundle(
		    FhirProviderUtils.convertSearchResultsToBundle(practitionerService.findPractitionerByIdentifier(identifier)));
	}
	
}