package com.example.olave.inriego;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import Persistencia.Json_SQLiteHelper;
import Persistencia.SQLiteHelper;

/**
 * Created by prog on 07/10/2017.
 */

public class SendMail extends AsyncTask<Void,Void,Integer> {

    //Declaring Variables
    private Context context;
    private Session session;


    //Information to send email
    private String email;
    private String subject;
    private String message;

    //Progressdialog to show while sending email
    private ProgressDialog progressDialog;


    //Class Constructor
    public SendMail(Context context, String email, String subject, String message){
        //Initializing variables
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(context,"Sending mail", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Integer res) {
       super.onPostExecute(res);

        //pregunto si se envio bien el mail
        if(res==1){
            Json_SQLiteHelper json_sq= new Json_SQLiteHelper(context, "DBJsons", null, 1);
            SQLiteDatabase dta_base = json_sq.getReadableDatabase();
            SQLiteHelper abd = new SQLiteHelper(dta_base, json_sq);
            if(subject.contains("jornada")){
                abd.borrarLog(dta_base,json_sq);
            }
            if(subject.contains("registros")){
                abd.borrar(dta_base,json_sq);
            }
        }


        //pregunto que tipo de mail se envio


        //Showing a success message
        //Toast.makeText(context,"Se envio mail a la empresa con los datos pendientes a sincronizar", Toast.LENGTH_LONG).show();
    }




    @Override
    protected Integer doInBackground(Void... params) {
        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        //If you are not using gmail you may need to change the values
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    //Authenticating the password
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("inriegoapplication@gmail.com", "inriego2017");
                    }
                });

        try {
            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
            mm.setFrom(new InternetAddress("inriegoapplication@gmail.com"));
            //Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            //Adding subject
            mm.setSubject(subject);
            //Adding message
            //mm.setText(message,"text/html; charset=utf-8");
            mm.setContent(message,"text/html; charset=utf-8");
            //Sending email
            Transport.send(mm);

            return 1;
        } catch (MessagingException e) {
            e.printStackTrace();
            return 0;
        }

    }

}
