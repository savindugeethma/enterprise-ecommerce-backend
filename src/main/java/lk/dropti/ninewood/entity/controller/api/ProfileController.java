package lk.dropti.ninewood.entity.controller.api;

import com.google.gson.internal.bind.util.ISO8601Utils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lk.dropti.ninewood.annotation.IsUser;
import lk.dropti.ninewood.dto.UserDTO;
import lk.dropti.ninewood.service.ProfileService;
import lk.dropti.ninewood.util.AppUtil;

@Path("/profiles")
public class ProfileController {


    /// ////// tiye
    @IsUser
    @Path("/addresses")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadAddresses(@Context HttpServletRequest request) {
        String responseJson = new ProfileService().loadAddresses(request);
        return Response.ok().entity(responseJson).build();
    }



//    @IsUser
//    @Path("/update-profile")
//    @PUT
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response updateUserProfile(String jsonData, @Context HttpServletRequest request) {
//        UserDTO userDTO = AppUtil.GSON.fromJson(jsonData, UserDTO.class);
//        String responseJson = new ProfileService().updateProfile(userDTO, request);
//        return Response.ok().entity(responseJson).build();
//    }



    @IsUser
    @Path("/user-profile")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response loadUserProfile(@Context HttpServletRequest request) {
        String responseJson = new ProfileService().userProfile(request);
        return Response.ok().entity(responseJson).build();
    }
}
