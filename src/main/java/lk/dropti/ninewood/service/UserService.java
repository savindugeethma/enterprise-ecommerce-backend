package lk.dropti.ninewood.service;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Context;
import lk.dropti.ninewood.dto.UserDTO;
import lk.dropti.ninewood.entity.Status;
import lk.dropti.ninewood.entity.User;
import lk.dropti.ninewood.util.AppUtil;
import lk.dropti.ninewood.util.HibernateUtil;
import lk.dropti.ninewood.validation.Validator;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class UserService {

    public String userLogin(UserDTO userDTO, @Context HttpServletRequest request) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message = "";

        ///  login handling code | user authentication part-start
        if (userDTO.getEmail() == null) {
            message = "Email is required!";
        } else if (userDTO.getEmail().isBlank()) {
            message = "Email address can not be empty!";
        } else if (!userDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
            message = "Please provide valid email address!";
        } else if (userDTO.getPassword() == null) {
            message = "Password is required!";
        } else if (userDTO.getPassword().isBlank()) {
            message = "Password can not be empty!";
        } else if (!userDTO.getPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "Please provide valid password. \n " +
                    "The password must be at least 8 characters long and include at least one uppercase letter, " +
                    "one lowercase letter, one digit, and one special character";
//        } else {
//            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//            User user = hibernateSession.createQuery("FROM User u WHERE u.email=:email", User.class)
//                    .setParameter("email", userDTO.getEmail())
////                    .setParameter("verificationCode", userDTO.getVerificationCode())
////                    .getSingleResultOrNull();
//            if (user == null) {
//                message = "Account not found. Please register first!";
//            } else {
//                System.out.println("done");
////                Status verifiedStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
////                        .setParameter("value", String.valueOf(Status.Type.VERIFIED))
////                        .getSingleResult();
////
////                if (user.getStatus().equals(verifiedStatus)) {
////                    message = "Account already verified!";
////                } else {
////                    user.setStatus(verifiedStatus);
////                    user.setVerificationCode("");
//                    Transaction transaction = hibernateSession.beginTransaction();
//                    try {
//                        hibernateSession.merge(user);
//                        transaction.commit();
//                        status = true;
//                        message = "Account verification completed!";
//                    } catch (HibernateException e) {
//                        transaction.rollback();
//                        message = "Something went wrong. Verification process failed!";
//                    }
////                }
//            }
//            hibernateSession.close();

        } else {
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            User singleUser = hibernateSession.createNamedQuery("User.getByEmail", User.class)
                    .setParameter("email", userDTO.getEmail())
                    .getSingleResultOrNull();
            if (singleUser == null) { // not found
                message = "Account not found. Please register first!";
            } else {
                if (!singleUser.getPassword().equals(userDTO.getPassword())) {
                    message = "Something went wrong. Please check your login credentials!";
                } else {
//                    Status verifiedStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
//                            .setParameter("value", String.valueOf(Status.Type.VERIFIED))
//                            .getSingleResult();
                    Status verifiedStatus = hibernateSession
                            .createNamedQuery("Status.findByValue", Status.class)
                            .setParameter("value", String.valueOf(Status.Type.VERIFIED))
                            .getSingleResult();

                    if (!singleUser.getStatus().equals(verifiedStatus)) {
                        message = "Your account is not verified. Please verify first!";
                    } else {
                        HttpSession httpSession = request.getSession();
                        httpSession.setAttribute("user", singleUser);
                        status = true;
                        message = "Login successful";
                    }
                }
            }
            hibernateSession.close();
        }
        ///  login handling code | user authentication part-end


        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

//    public String verifyUserAccount(UserDTO userDTO) {
//        JsonObject responseObject = new JsonObject();
//        boolean status = false;
//        String message = "";
//
//        ///  logic handling part
//        if (userDTO.getEmail() == null) {
//            message = "Email is required!";
//        } else if (userDTO.getEmail().isBlank()) {
//            message = "Email address can not be empty!";
//        } else if (!userDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
//            message = "Please provide valid email address!";
//        } else if (userDTO.getVerificationCode() == null) {
//            message = "Verification is required!";
//        } else if (userDTO.getVerificationCode().isBlank()) {
//            message = "Verification code can not be empty!";
//        } else if (!userDTO.getVerificationCode().matches(Validator.VERIFICATION_CODE_VALIDATION)) {
//            message = "Please provide valid verification code!. Verification code must have 6 digits";
//        } else {
//            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//            User user = hibernateSession.createQuery("FROM User u WHERE u.email=:email AND u.verificationCode=:verificationCode", User.class)
//                    .setParameter("email", userDTO.getEmail())
//                    .setParameter("verificationCode", userDTO.getVerificationCode())
//                    .getSingleResultOrNull();
//            if (user == null) {
//                message = "Account not found. Please register first!";
//            } else {
//                Status verifiedStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
//                        .setParameter("value", String.valueOf(Status.Type.VERIFIED))
//                        .getSingleResult();
//
//                if (user.getStatus().equals(verifiedStatus)) {
//                    message = "Account already verified!";
//                } else {
//                    user.setStatus(verifiedStatus);
//                    user.setVerificationCode("");
//                    Transaction transaction = hibernateSession.beginTransaction();
//                    try {
//                        hibernateSession.merge(user);
//                        transaction.commit();
//                        status = true;
//                        message = "Account verification completed!";
//                    } catch (HibernateException e) {
//                        transaction.rollback();
//                        message = "Something went wrong. Verification process failed!";
//                    }
//                }
//            }
//            hibernateSession.close();
//        }
//
//        responseObject.addProperty("status", status);
//        responseObject.addProperty("message", message);
//        return AppUtil.GSON.toJson(responseObject);
//    }



//    public String addNewUser(UserDTO userDTO) {
//        JsonObject responseObject = new JsonObject();
//
//        boolean status = false;
//        String message;
//
//        if (userDTO.getFirstName() == null) {
//            message = "First name is required!";
//        } else if (userDTO.getFirstName().isBlank()) {
//            message = "First name can not be empty!";
//        } else if (userDTO.getLastName() == null) {
//            message = "Last name is required!";
//        } else if (userDTO.getLastName().isBlank()) {
//            message = "Last name can not be empty!";
//        } else if (userDTO.getEmail() == null) {
//            message = "Email is required!";
//        } else if (userDTO.getEmail().isBlank()) {
//            message = "Email can not be empty!";
//        } else if (!userDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
//            message = "Please provide valid email address!";
//        } else if (userDTO.getPassword() == null) {
//            message = "Password is required!";
//        } else if (userDTO.getPassword().isBlank()) {
//            message = "Password can not be empty!";
//        } else if (!userDTO.getPassword().matches(Validator.PASSWORD_VALIDATION)) {
//            message = "Please provide valid password. \n " +
//                    "The password must be at least 8 characters long and include at least one uppercase letter, " +
//                    "one lowercase letter, one digit, and one special character";
//        } else {
//            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
//            User singleUser = hibernateSession.createNamedQuery("User.getByEmail", User.class)
//                    .setParameter("email", userDTO.getEmail())
//                    .getSingleResultOrNull();
//
//            if (singleUser != null) { // Already exists
//                message = "This email already exists! Please use another email";
//            } else {
//                User u = new User();
//                u.setFirstName(userDTO.getFirstName());
//                u.setLastName(userDTO.getLastName());
//                u.setEmail(userDTO.getEmail());
//                u.setPassword(userDTO.getPassword());
//
//                String verificationCode = AppUtil.generateCode();
//
////                u.setVerificationCode(verificationCode);
//
//                Status pendingStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
//                        .setParameter("value", String.valueOf(Status.Type.PENDING)).getSingleResult();
//
//                u.setStatus(pendingStatus);
//
//                Transaction transaction = hibernateSession.beginTransaction();
//
//                try {
//                    hibernateSession.persist(u);
//                    transaction.commit();
//
//                    /// verification-mail-sending-start
////                    VerificationMail verificationMail = new VerificationMail(u.getEmail(), verificationCode);
////                    MailServiceProvider.getInstance().sendMail(verificationMail);
//                    /// verification-mail-sending-end
//
//                    status = true;
//                    message = "Account created successfully. Verification code has been sent to the your email. " +
//                            "Please verify it for activate your account!";
//
//
//                } catch (HibernateException e) {
//                    transaction.rollback();
//                    message = "Account creation failed. Please try again!";
//                }
//
//
//            }
//            hibernateSession.close();
//        }
//        responseObject.addProperty("status", status);
//        responseObject.addProperty("message", message);
//        return AppUtil.GSON.toJson(responseObject);
//    }


    public String verifyUserAccount(UserDTO userDTO) {
    JsonObject responseObject = new JsonObject();
    boolean status = false;
    String message = "";

    if (userDTO.getEmail() == null) {
        message = "Email is required!";
    } else if (userDTO.getEmail().isBlank()) {
        message = "Email address can not be empty!";
    } else if (!userDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
        message = "Please provide valid email address!";
    } else if (userDTO.getVerificationCode() == null) {
        message = "Verification is required!";
    } else if (userDTO.getVerificationCode().isBlank()) {
        message = "Verification code can not be empty!";
    } else if (!userDTO.getVerificationCode().matches(Validator.VERIFICATION_CODE_VALIDATION)) {
        message = "Please provide valid verification code!. Verification code must have 6 digits";
    } else {
        Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
        User user = hibernateSession.createQuery("FROM User u WHERE u.email=:email AND u.verificationCode=:verificationCode", User.class)
                .setParameter("email", userDTO.getEmail())
                .setParameter("verificationCode", userDTO.getVerificationCode())
                .getSingleResultOrNull();
        if (user == null) {
            message = "Account not found. Please register first!";
        } else {
            Status verifiedStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                    .setParameter("value", String.valueOf(Status.Type.VERIFIED))
                    .getSingleResult();

            if (user.getStatus().equals(verifiedStatus)) {
                message = "Account already verified!";
            } else {
                user.setStatus(verifiedStatus);
                user.setVerificationCode(""); // 💡 වෙරිෆයි වුණාට පස්සේ කෝඩ් එක හිස් කරනවා
                Transaction transaction = hibernateSession.beginTransaction();
                try {
                    hibernateSession.merge(user);
                    transaction.commit();
                    status = true;
                    message = "Account verification completed!";
                } catch (HibernateException e) {
                    transaction.rollback();
                    message = "Something went wrong. Verification process failed!";
                }
            }
        }
        hibernateSession.close();
    }

    responseObject.addProperty("status", status);
    responseObject.addProperty("message", message);
    return AppUtil.GSON.toJson(responseObject);
}

    public String addNewUser(UserDTO userDTO) {
        JsonObject responseObject = new JsonObject();
        boolean status = false;
        String message;

        if (userDTO.getFirstName() == null) {
            message = "First name is required!";
        } else if (userDTO.getFirstName().isBlank()) {
            message = "First name can not be empty!";
        } else if (userDTO.getLastName() == null) {
            message = "Last name is required!";
        } else if (userDTO.getLastName().isBlank()) {
            message = "Last name can not be empty!";
        } else if (userDTO.getEmail() == null) {
            message = "Email is required!";
        } else if (userDTO.getEmail().isBlank()) {
            message = "Email can not be empty!";
        } else if (!userDTO.getEmail().matches(Validator.EMAIL_VALIDATION)) {
            message = "Please provide valid email address!";
        } else if (userDTO.getPassword() == null) {
            message = "Password is required!";
        } else if (userDTO.getPassword().isBlank()) {
            message = "Password can not be empty!";
        } else if (!userDTO.getPassword().matches(Validator.PASSWORD_VALIDATION)) {
            message = "Please provide valid password. \n " +
                    "The password must be at least 8 characters long and include at least one uppercase letter, " +
                    "one lowercase letter, one digit, and one special character";
        } else {
            Session hibernateSession = HibernateUtil.getSessionFactory().openSession();
            User singleUser = hibernateSession.createNamedQuery("User.getByEmail", User.class)
                    .setParameter("email", userDTO.getEmail())
                    .getSingleResultOrNull();

            if (singleUser != null) {
                message = "This email already exists! Please use another email";
            } else {
                User u = new User();
                u.setFirstName(userDTO.getFirstName());
                u.setLastName(userDTO.getLastName());
                u.setEmail(userDTO.getEmail());
                u.setPassword(userDTO.getPassword());

                String verificationCode = AppUtil.generateCode();
                u.setVerificationCode(verificationCode);

                Status pendingStatus = hibernateSession.createNamedQuery("Status.findByValue", Status.class)
                        .setParameter("value", String.valueOf(Status.Type.PENDING)).getSingleResult();
                u.setStatus(pendingStatus);

                Transaction transaction = hibernateSession.beginTransaction();

                try {
                    hibernateSession.persist(u);
                    transaction.commit();

                    try {
                        lk.dropti.ninewood.mail.VerificationMail verificationMail = new lk.dropti.ninewood.mail.VerificationMail(u.getEmail(), verificationCode);
                        lk.dropti.ninewood.provider.MailServiceProvider.getInstance().sendMail(verificationMail);
                        System.out.println(" Verification mail queued for: " + u.getEmail());
                    } catch (Exception mailException) {
                        System.out.println(" Mail queueing failed, but user account created.");
                        mailException.printStackTrace();
                    }

                    status = true;
                    message = "Account created successfully. Verification code has been sent to your email. Please verify it to activate your account!";

                } catch (HibernateException e) {
                    transaction.rollback();
                    message = "Account creation failed. Please try again!";
                }
            }
            hibernateSession.close();
        }
        responseObject.addProperty("status", status);
        responseObject.addProperty("message", message);
        return AppUtil.GSON.toJson(responseObject);
    }

    public boolean verifyAndActivateUser(String email, String code) {
        org.hibernate.SessionFactory sessionFactory = lk.dropti.ninewood.util.HibernateUtil.getSessionFactory();
        org.hibernate.Session session = sessionFactory.openSession();
        org.hibernate.Transaction transaction = null;
        boolean isSuccess = false;

        try {
            transaction = session.beginTransaction();

            org.hibernate.query.Query<lk.dropti.ninewood.entity.User> query =
                    session.createQuery("FROM User WHERE email = :email", lk.dropti.ninewood.entity.User.class);
            query.setParameter("email", email);
            lk.dropti.ninewood.entity.User user = query.uniqueResult();

            if (user != null) {
                System.out.println("[USER SERVICE]: Found User in DB. Input Code: " + code + " | DB Code: " + user.getVerificationCode());

                if (user.getVerificationCode() != null && user.getVerificationCode().equals(code)) {


                    lk.dropti.ninewood.entity.Status activeStatus = session.get(lk.dropti.ninewood.entity.Status.class, 3);

                    if (activeStatus != null) {
                        user.setStatus(activeStatus);
                        user.setVerificationCode(null);

                        session.update(user);
                        isSuccess = true;
                        System.out.println("[USER SERVICE]: Preparing to commit changes for " + email);
                    } else {
                        System.out.println("[USER SERVICE]: Status ID 3 not found in DB!");
                        isSuccess = false;
                    }
                } else {
                    System.out.println("[USER SERVICE]: Code Mismatch! Verification stopped.");
                    isSuccess = false;
                }
            } else {
                System.out.println("[USER SERVICE]: User not found for email: " + email);
                isSuccess = false;
            }

            if (isSuccess) {
                transaction.commit();
                System.out.println("[USER SERVICE]: Transaction COMMITTED successfully in DB!");
            } else {
                if (transaction != null) transaction.rollback();
            }

        } catch (Exception e) {
            isSuccess = false; //
            if (transaction != null) {
                try { transaction.rollback(); } catch(Exception ex) {}
            }
            System.out.println("[USER SERVICE EXCEPTION]: Database update crashed!");
            e.printStackTrace();
        } finally {
            session.close();
        }

        return isSuccess;
    }
}


