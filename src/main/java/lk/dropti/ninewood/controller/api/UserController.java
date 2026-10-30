package lk.dropti.ninewood.controller.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.dropti.ninewood.annotation.IsUser;
import lk.dropti.ninewood.dto.UserDTO;
import lk.dropti.ninewood.service.UserService;
import lk.dropti.ninewood.util.AppUtil;

@Path("/users")
public class UserController {

    @IsUser
    @Path("/logout")
    @GET
    public Response logout(@Context HttpServletRequest request) {

        HttpSession httpSession = request.getSession(false);
        if (httpSession != null && httpSession.getAttribute("user") != null) {
            httpSession.invalidate();
            return Response.status(Response.Status.OK).build();
        } else {
            System.out.println("else");
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewAccount(String jsonData) {
        UserDTO userDTO = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().addNewUser(userDTO);
        return Response.ok().entity(responseJson).build();
    }

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response userLogin(String jsonData, @Context HttpServletRequest request) {
        System.out.println("done controller");
        UserDTO userDTO = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
        String responseJson = new UserService().userLogin(userDTO, request);
        return Response.ok().entity(responseJson).build();
    }

    @jakarta.ws.rs.POST
    @jakarta.ws.rs.Path("/verify")
    @jakarta.ws.rs.Consumes(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    @jakarta.ws.rs.Produces(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
    public jakarta.ws.rs.core.Response verifyAccount(String jsonString) {

        System.out.println("📥 [USER CONTROLLER]: Received JSON String: " + jsonString);

        String email = "";
        String code = "";

        try {
            com.google.gson.JsonObject jsonObject = com.google.gson.JsonParser.parseString(jsonString).getAsJsonObject();
            email = jsonObject.get("email").getAsString();
            code = jsonObject.get("code").getAsString();

        } catch (NoClassDefFoundError | Exception e) {
            try {
                org.json.JSONObject jsonObject = new org.json.JSONObject(jsonString);
                email = jsonObject.getString("email");
                code = jsonObject.getString("code");
            } catch (Exception ex) {
                System.out.println(" [JSON PARSE ERROR]: Could not parse JSON data!");
                ex.printStackTrace();
            }
        }

        System.out.println(" [USER CONTROLLER] Extracted -> Email: " + email + ", Code: " + code);

        UserService userService = new UserService();
        boolean isVerified = userService.verifyAndActivateUser(email, code);

        String responseJson = "";

        if (isVerified) {
            responseJson = "{\"status\":true,\"message\":\"Account Activated Successfully! You can now log in.\"}";
            return jakarta.ws.rs.core.Response.ok(responseJson, jakarta.ws.rs.core.MediaType.APPLICATION_JSON).build();
        } else {
            responseJson = "{\"status\":false,\"message\":\"Invalid verification code or email address.\"}";
            return jakarta.ws.rs.core.Response.status(jakarta.ws.rs.core.Response.Status.BAD_REQUEST)
                    .entity(responseJson)
                    .type(jakarta.ws.rs.core.MediaType.APPLICATION_JSON)
                    .build();
        }
    }
}
