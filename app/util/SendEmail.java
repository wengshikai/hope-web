package util;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import play.Configuration;
import play.Play;

import java.util.List;

/**
 * Created by shanmao on 15-9-29.
 */
public class SendEmail {
    Email email = new SimpleEmail();


    public SendEmail(String smtpHost,int smtpPort,String userName,String password,String fromEmail,String fromName) throws EmailException {
        email.setHostName(smtpHost);
        email.setSmtpPort(smtpPort);
        email.setAuthenticator(new DefaultAuthenticator(userName, password));
        email.setSSLOnConnect(true);
        email.setFrom(fromEmail, fromName, "UTF-8");
    }

    public void Send(String subject,String msg,String receiver) throws EmailException {
        email.setSubject(subject);
        email.setMsg(msg);
        email.addTo(receiver);
        email.send();
    }


    public void Send(String subject,String msg,String[] receivers) throws EmailException {
        email.setSubject(subject);
        email.setMsg(msg);
        for(String s:receivers){
            email.addTo(s);
        }
        email.send();
    }

    public void Send(String subject,String msg,List<String> receivers) throws EmailException {
        email.setSubject(subject);
        email.setMsg(msg);
        for(String s:receivers){
            email.addTo(s);
        }
        email.send();
    }

    private static SendEmail getDefaultEmailSender(){
        SendEmail email_sender = null;
        Configuration config = Play.application().configuration();
        try {
            email_sender = new SendEmail(config.getString("custom.email.smtpHost"),
                    config.getInt("custom.email.smtpPort"),config.getString("custom.email.userName"),
                    config.getString("custom.email.password"),config.getString("custom.email.fromEmail"),
                    config.getString("custom.email.fromName"));
        } catch (EmailException e) {
            e.printStackTrace();
        }
        return email_sender;
    }

    public static void SendEmail(String subject,String msg,String receiver) throws EmailException {
        getDefaultEmailSender().Send(subject, msg, receiver);
    }

    public static void SendEmail(String subject,String msg,List<String> receivers) throws EmailException {
        getDefaultEmailSender().Send(subject, msg, receivers);
    }

    public static void SendEmail(String subject,String msg,String[] receivers) throws EmailException {
        getDefaultEmailSender().Send(subject,msg,receivers);
    }

}
