package com.github.rafaeldcfarias.operationsongitlab.resources;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.rafaeldcfarias.operationsongitlab.service.OperationsService;

@RestController
@RequestMapping("/operations")
public class OperationsResource {

	private OperationsService operationsService;

	public OperationsResource(OperationsService operationsService) {
		this.operationsService = operationsService;
	}

	@GetMapping("/protect-masters-from-direct-push/{groupId}")
	public void protectMastersOfGroupFromDirectPush(@PathVariable Long groupId) {
		operationsService.protectMastersOfGroupFromDirectPush(groupId);
	}
}
