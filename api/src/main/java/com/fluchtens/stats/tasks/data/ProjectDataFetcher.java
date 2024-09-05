package com.fluchtens.stats.tasks.data;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fluchtens.stats.models.Project;
import com.fluchtens.stats.repositories.ProjectRepository;

@Component
public class ProjectDataFetcher extends DataFetcher {
    @Autowired
    private ProjectRepository projectRepository;

    private void print(String message, boolean error) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime time = LocalDateTime.now();
        String formattedTime = time.format(formatter);

        String CYAN = "\033[1;36m";
        String RED = "\033[0;31m";
        String GREEN = "\033[0;32m";
        String RESET = "\033[0m";
        String WHITE = "\u001B[37m";

        String TEXT = GREEN;
        if (error) {
            TEXT = RED;
        }

        System.out.println(CYAN + "[ProjectDataFetcher]" + WHITE + " [" + formattedTime + "] " + TEXT + message + RESET);
    }

    private JSONArray fetchProjectsPage(int page) {
        try {
            String apiUrl = String.format(this.apiUrl + "/projects?page[number]=%d&page[size]=100&cursus_id=21&sort=id", page);
            URI uri = new URI(apiUrl);
            URL url = uri.toURL();

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + this.accessToken);

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                this.print(connection.getResponseCode() + " " + connection.getResponseMessage() + ", retry in 15 seconds...", true);
                Thread.sleep(15 * 1000);
                return this.fetchProjectsPage(page);
            }

            StringBuilder response = new StringBuilder();
            BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = bf.readLine()) != null) {
                response.append(line);
            }
            bf.close();
            return new JSONArray(response.toString());
        } catch (Exception e) {
            this.print("fetchProjectsPage() catched exception" + e.getMessage(), true);
            return new JSONArray();
        }
    }

    protected void fetchAllProjects() {
        int page = 1;

        while (true) {
            JSONArray projectsJson = this.fetchProjectsPage(page);
            if (projectsJson.length() <= 0) {
                break;
            }

            for (int i = 0; i < projectsJson.length(); i++) {
                JSONObject projectJson = projectsJson.getJSONObject(i);

                int id = -1;
                String name = null;
                String slug = null;
                int difficulty = -1;

                if (!projectJson.isNull("id")) 
                    id = projectJson.getInt("id");
                if (!projectJson.isNull("name")) 
                    name = projectJson.getString("name");
                if (!projectJson.isNull("slug")) 
                    slug = projectJson.getString("slug");
                if (!projectJson.isNull("difficulty")) 
                    difficulty = projectJson.getInt("difficulty");

                if (id <= 0 || name == null || slug == null || difficulty <= 0) {
                    continue;
                }

                if (this.projectRepository.existsById(id)) {
                    continue;
                }

                if (this.projectRepository.existsBySlug(slug)) {
                    continue;
                }

                if (this.projectRepository.existsByName(name)) {
                    continue;
                }

                Project project = new Project();
                project.setId(id);
                project.setName(name);
                project.setSlug(slug);
                project.setDifficulty(difficulty);
                this.projectRepository.save(project);
            }
            page++;
        }
    }
}
