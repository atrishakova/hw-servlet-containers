package ru.netology.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import ru.netology.model.Post;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import ru.netology.exception.NotFoundException;

public class PostController {
    public static final String APPLICATION_JSON = "application/json";
    private final PostService service;

    private static final Gson GSON = new Gson();

    public PostController(PostService service) {
        this.service = service;
    }

    public void all(HttpServletResponse response) throws IOException {
        writeJsonResponse(response, service.all());
    }

    public void getById(long id, HttpServletResponse response) throws IOException {
        try {
            Post post = service.getById(id);
            writeJsonResponse(response, post);
        } catch (NotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    public void save(Reader body, HttpServletResponse response) throws IOException {
        try {
            Post post = GSON.fromJson(body, Post.class);

            if (post == null) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid post data");
                return;
            }

            Post savedPost = service.save(post);
            writeJsonResponse(response, savedPost);
        } catch (JsonSyntaxException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid JSON format");
        } catch (NotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    public void removeById(long id, HttpServletResponse response) throws IOException {
        try {
            service.removeById(id);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    private void writeJsonResponse(HttpServletResponse response, Object data) throws IOException {
        response.setContentType(APPLICATION_JSON);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().print(GSON.toJson(data));
    }

    private void sendError(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType(APPLICATION_JSON);
        response.getWriter().print(GSON.toJson(new ErrorResponse(statusCode, message)));
    }

    private static class ErrorResponse {
        private final int statusCode;
        private final String message;

        public ErrorResponse(int statusCode, String message) {
            this.statusCode = statusCode;
            this.message = message;
        }
    }
}
