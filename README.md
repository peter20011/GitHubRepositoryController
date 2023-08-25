# GitHub Repository Controller

This Spring Boot controller provides an API endpoint to retrieve non-forked repositories and their branches for a given GitHub user.

## Endpoints

### Get User Repositories and Branches

**Endpoint:** `/api/github/repositories/{username}`
**HTTP Method:** GET

#### Request

No request parameters are required. The `{username}` path parameter specifies the GitHub username for which repositories are to be fetched.

#### Response

- **200 OK**: Returns a JSON list of repositories and their branches.

    ```json
    [
        {
            "Repository Name": "example-repo",
            "Owner": "example-user",
            "Branches": [
                {
                    "name": "main",
                    "last_commit_sha": "a1b2c3d4"
                },
                {
                    "name": "feature-branch",
                    "last_commit_sha": "e5f6g7h8"
                }
            ]
        },
        {
            "Repository Name": "another-repo",
            "Owner": "example-user",
            "Branches": [
                {
                    "name": "main",
                    "last_commit_sha": "i9j0k1l2"
                }
            ]
        }
        
    ]
    ```

- **404 Not Found**: If no repositories are found for the given user, the response will be:

    ```json
    {
        "status": 404,
        "Message": "No repositories found for this user"
    }
    ```

- **500 Internal Server Error**: If there is a server error while fetching repositories, the response will be:

    ```json
    {
        "status": 500,
        "Message": "Internal server error message"
    }
    ```

- **406 Not Acceptable**: If the requested content type is not supported (other than JSON), the response will be:

    ```json
    {
        "status": 406,
        "Message": "Requested content type is not acceptable"
    }
    ```

Please note that this controller uses the GitHub REST API to retrieve repository and branch information. It filters out forked repositories and provides branch details for the remaining repositories.

For usage, make sure to replace `{username}` with the actual GitHub username in the endpoint URL.

For further information on how to use and integrate this controller, refer to the official Spring Boot documentation.

To run this Spring Boot application locally, follow these steps:

1. **Clone the Repository**: Clone this repository to your local machine using Git:
2. **Build the Application**: Navigate to the root directory of the project and build the application using Maven:
    ```shell
    mvn clean install
    ```
3. **Run the Application**: Run the application using Maven:
    ```shell
       mvn spring-boot:run
        ```
4. **Test the Application**: Test the application using cURL or any other HTTP client:
    ```shell
    curl -X GET http://localhost:8080/api/github/repositories/{username}
    ```
5. **Stop the Application**: Stop the application by pressing `Ctrl + C` in the terminal window where the application is running.