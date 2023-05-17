package com.best.electronics.email;

import com.best.electronics.database.IDatabasePersistence;
import com.best.electronics.database.MySQLDatabasePersistence;
import com.best.electronics.forgotPassword.GetCode;
import com.best.electronics.repository.PasswordRepository;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Map;

public class SendMailForForgotPassword implements GetCode {

    Session newSession = null;
    MimeMessage mimeMessage = null;

    @Override
    public void generateCode(String type ,String medium) throws Exception {
        emailControl(type, medium);
    }

    public void emailControl (String type, String email) throws Exception {
        GenerateRandomNumber generateRandomNumber = GenerateRandomNumber.getInstance();
        int randomNumber = generateRandomNumber.generateRandomNumber();
        SendMail sendMail = new SendMail();
        newSession = sendMail.setUpProperties();
        IDatabasePersistence databasePersistence = new MySQLDatabasePersistence();
        PasswordRepository passwordRepository = new PasswordRepository(databasePersistence);
        ArrayList<Map<String, Object>> result;
        result = passwordRepository.getEmailCheck(email,type);
        if(result.size()== 1){
            //checking if email is valid or not, if valid set random token, draft email and send.
            saveToDB(randomNumber,email,type);
            draftEmail(randomNumber,email,type);
            sendMail.setMimeMessage(mimeMessage);
            sendMail.sendMail();
        }
    }

    public void draftEmail(int randomNumber, String email, String type) throws MessagingException {
        String emailSubject = "Token for new password request";
        mimeMessage = new MimeMessage(newSession);
        mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
        mimeMessage.setSubject(emailSubject);
        mimeMessage.setText("We have received a password change request from your ID. Below is your code: "+randomNumber+". " +
                "Go to below link to change your password: http://localhost:8080/"+type.toLowerCase()+"/resetPassword");
    }
    private void saveToDB(int randomNumber, String email,String type){
        EmailControllerPinResetStore emailControllerPinResetStore = new EmailControllerPinStoreHandler();
        emailControllerPinResetStore.storePinToDB(randomNumber,email,type);
    }
}
