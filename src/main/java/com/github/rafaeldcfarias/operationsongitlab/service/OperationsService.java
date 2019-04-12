package com.github.rafaeldcfarias.operationsongitlab.service;

import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.github.rafaeldcfarias.operationsongitlab.domain.dto.ProjectDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OperationsService {

	private static final String PRIVATE_TOKEN_HEADER_NAME = "PRIVATE-TOKEN";

	@Value("${gitlab.private_token}")
	private String privateToken;

	@Value("${gitlab.domain}")
	private String domain;

	private RestTemplate restTemplate;

	public OperationsService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Async
	public void protectMastersOfGroupFromDirectPush(Long groupId) {

		String groupProjectsURL = "{1}/api/v4/groups/{0}/projects?per_page=100";
		String removingMasterProetctionURL = "{1}/api/v4/projects/{0}/protected_branches/master";
		String protectingMasterURL = "{1}/api/v4/projects/{0}/protected_branches?name=master&push_access_level=0&merge_access_level=40";

		ParameterizedTypeReference<List<ProjectDTO>> parameterizedTypeReference = new ParameterizedTypeReference<List<ProjectDTO>>() {
		};
		restTemplate
				.exchange(format(groupProjectsURL, groupId, domain), GET, getHttpEntityWithHeaders(),
						parameterizedTypeReference)
				.getBody().stream()//
				.forEach(project -> {
					log.info(project.toString());
					try {
						ResponseEntity<String> removingRequest = restTemplate.exchange(
								format(removingMasterProetctionURL, project.getId(), domain), DELETE,
								getHttpEntityWithHeaders(), String.class);
						log.info("Removing status: " + removingRequest.getStatusCode().toString());
					} catch (Exception e) {
						log.error("Fail removing: ", e);
					}
					try {
						ResponseEntity<String> protectingRequest = restTemplate.exchange(
								format(protectingMasterURL, project.getId(), domain), POST, getHttpEntityWithHeaders(),
								String.class);
						log.info(protectingRequest.getBody() + " : " + protectingRequest.getStatusCode().toString());
					} catch (Exception e) {
						log.error("Fail protecting: ", e);
					}

				});
	}

	private HttpEntity<String> getHttpEntityWithHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(asList(APPLICATION_JSON));
		headers.set(PRIVATE_TOKEN_HEADER_NAME, privateToken);

		return new HttpEntity<>("parameters", headers);
	}

}
