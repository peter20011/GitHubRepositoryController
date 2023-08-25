package com.example.task;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/github")
public class GitHubRepositoryController {

    private final String GITHUB_API_BASE_URL = "https://api.github.com";
    Logger logger = LoggerFactory.getLogger(GitHubRepositoryController.class);

    @GetMapping(value = "/repositories/{username}")
    public ResponseEntity<?> getUserRepositories(@PathVariable String username, HttpServletRequest request) {

        String requestedContentType = request.getHeader("Accept");

        if (MediaType.APPLICATION_XML_VALUE.equals(requestedContentType)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("status", HttpStatus.NOT_ACCEPTABLE.value(), "Message", "Requested content type is not acceptable"));
        }


        String repositoriesUrl = GITHUB_API_BASE_URL + "/users/" + username + "/repos";
        RestTemplate restTemplate = new RestTemplate();

        try {

            ResponseEntity<Object[]> response = restTemplate.getForEntity(repositoriesUrl, Object[].class);
            Object[] repositories = response.getBody();

            if (repositories == null || repositories.length == 0) {
                throw new Exception("No repositories found for user");
            }


            List<Map<String, Object>> result = new ArrayList<>();

            for (Object repo : repositories) {
                Map<String, Object> repoMap = (Map<String, Object>) repo;

                if (!(boolean) repoMap.get("fork")) {
                    String repoName = (String) repoMap.get("name");
                    String ownerLogin = (String) ((Map<String, Object>) repoMap.get("owner")).get("login");

                    String branchesUrl = (String) repoMap.get("branches_url");
                    branchesUrl = branchesUrl.substring(0, branchesUrl.indexOf("{"));

                    ResponseEntity<Object[]> branchesResponse = restTemplate.getForEntity(branchesUrl, Object[].class);
                    Object[] branches = branchesResponse.getBody();

                    List<Map<String, String>> branchDetails = new ArrayList<>();

                    for (Object branch : branches) {
                        Map<String, Object> branchMap = (Map<String, Object>) branch;
                        String branchName = (String) branchMap.get("name");
                        String lastCommitSha = (String) ((Map<String, Object>) branchMap.get("commit")).get("sha");

                        branchDetails.add(Map.of("name", branchName, "last_commit_sha", lastCommitSha));
                    }

                    result.add(Map.of("Repository Name", repoName,"Owner", ownerLogin,"Branches", branchDetails));
                }
            }

            return ResponseEntity.ok(result);
        } catch (HttpServerErrorException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("status", HttpStatus.INTERNAL_SERVER_ERROR.value(), "Message", e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("status", HttpStatus.NOT_FOUND.value(), "Message", "No repositories found for this user"));
        }
    }
}