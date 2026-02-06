package com.example.UberComp.utils;

import com.example.UberComp.model.Account;
import com.example.UberComp.model.SetPasswordToken;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EmailUtils {
    @Value("${spring.sendgrid.api-key}")
    private String sendGridApiKey;

    public void sendMail(String to, String subject, String content) throws IOException {
        Email from = new Email("cvorovicniksa@gmail.com");
        Email toEmail = new Email(to);
        Content emailContent = new Content("text/html", content);
        Mail mail = new Mail(from, subject, toEmail, emailContent);
        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        Response response = sg.api(request);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
    }

    public void sendVerificationEmail(Account account){
        String verificationLink = "http://localhost:4200/verify/" + account.getVerification();
        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Verify Your Account</title>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;\">\n" +
                "    <table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\" style=\"padding: 40px 0;\">\n" +
                "                <table role=\"presentation\" style=\"width: 600px; border-collapse: collapse; background-color: #ffffff; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\">\n" +
                "                    <!-- Header -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 40px 30px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); text-align: center;\">\n" +
                "                            <h1 style=\"margin: 0; color: #ffffff; font-size: 28px; font-weight: 600;\">Account Verification</h1>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    \n" +
                "                    <!-- Content -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 40px 30px;\">\n" +
                "                            <p style=\"margin: 0 0 20px; color: #333333; font-size: 16px; line-height: 1.6;\">\n" +
                "                                Hello!\n" +
                "                            </p>\n" +
                "                            <p style=\"margin: 0 0 30px; color: #333333; font-size: 16px; line-height: 1.6;\">\n" +
                "                                Verify your account by clicking on the button below:\n" +
                "                            </p>\n" +
                "                            \n" +
                "                            <!-- Button -->\n" +
                "                            <table role=\"presentation\" style=\"margin: 0 auto;\">\n" +
                "                                <tr>\n" +
                "                                    <td style=\"border-radius: 6px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\">\n" +
                "                                        <a href=\""+ verificationLink + "\" style=\"display: inline-block; padding: 16px 40px; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: 600; border-radius: 6px;\">\n" +
                "                                            Verify Account\n" +
                "                                        </a>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                            \n" +
                "                            <p style=\"margin: 30px 0 0; color: #666666; font-size: 14px; line-height: 1.6;\">\n" +
                "                                If the button doesn't work, you can copy and paste this link into your browser:\n" +
                "                            </p>\n" +
                "                            <p style=\"margin: 10px 0 0; color: #667eea; font-size: 14px; word-break: break-all;\">\n" +
                "                                " + verificationLink + "\n" +
                "                            </p>\n" +
                "                            <p style=\"margin: 30px 0 0; color: #666666; font-size: 14px; line-height: 1.6;\">\n" +
                "                                Verification link expires in 24 hours!\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    \n" +
                "                    <!-- Footer -->\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 30px; background-color: #f8f8f8; text-align: center; border-top: 1px solid #eeeeee;\">\n" +
                "                            <p style=\"margin: 0; color: #999999; font-size: 12px; line-height: 1.5;\">\n" +
                "                                If you didn't create an account, you can safely ignore this email.\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "</html>";
        String subject = "UberComp Account Verification";
        try {
            sendMail("cvorovicniksa@gmail.com", subject, body);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendSetPasswordEmail(SetPasswordToken token){
        String SetPasswordToken = "http://localhost:4200/set-password/" + token.getToken();
        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Set A Password For Your Account</title>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;\">\n" +
                "    <table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\" style=\"padding: 40px 0;\">\n" +
                "                <table role=\"presentation\" style=\"width: 600px; border-collapse: collapse; background-color: #ffffff; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\">\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 40px 30px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); text-align: center;\">\n" +
                "                            <h1 style=\"margin: 0; color: #ffffff; font-size: 28px; font-weight: 600;\">Set Password</h1>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 40px 30px;\">\n" +
                "                            <p style=\"margin: 0 0 20px; color: #333333; font-size: 16px; line-height: 1.6;\">Hello!</p>\n" +
                "                            <p style=\"margin: 0 0 30px; color: #333333; font-size: 16px; line-height: 1.6;\">\n" +
                "                                Set your new password by clicking on the button below and following the instructions:\n" +
                "                            </p>\n" +
                "                            <table role=\"presentation\" style=\"margin: 0 auto;\">\n" +
                "                                <tr>\n" +
                "                                    <td style=\"border-radius: 6px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\">\n" +
                "                                        <a href=\"" + SetPasswordToken + "\" style=\"display: inline-block; padding: 16px 40px; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: 600; border-radius: 6px;\">\n" +
                "                                            Set Password\n" +
                "                                        </a>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                            <p style=\"margin: 30px 0 0; color: #666666; font-size: 14px; line-height: 1.6;\">\n" +
                "                                Set Password link expires in 24 hours!\n" +
                "                            </p>\n" +
                "                            <p style=\"margin: 30px 0 0; color: #666666; font-size: 14px; line-height: 1.6;\">\n" +
                "                                If the button doesn't work, you can copy and paste this link into your browser:\n" +
                "                            </p>\n" +
                "                            <p style=\"margin: 10px 0 0; color: #667eea; font-size: 14px; word-break: break-all;\">\n" +
                "                                " + SetPasswordToken + "\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "</html>";

        String subject = "UberComp Account Set Password";
        try {
            sendMail("cvorovicniksa@gmail.com", subject, body);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendEmailWhenRideIsFinished(String email, Long rideId){
        String ratingLink = "http://localhost:4200/rating/"+rideId;
        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Ride Finished</title>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;\">\n" +
                "    <table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\" style=\"padding: 40px 0;\">\n" +
                "                <table role=\"presentation\" style=\"width: 600px; border-collapse: collapse; background-color: #ffffff; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\">\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 40px 30px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); text-align: center;\">\n" +
                "                            <h1 style=\"margin: 0; color: #ffffff; font-size: 28px; font-weight: 600;\">Ride Completed 🚗</h1>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 40px 30px;\">\n" +
                "                            <p style=\"margin: 0 0 20px; color: #333333; font-size: 16px; line-height: 1.6;\">Hello!</p>\n" +
                "                            <p style=\"margin: 0 0 30px; color: #333333; font-size: 16px; line-height: 1.6;\">\n" +
                "                                Your ride has been successfully completed. We hope you had a pleasant experience!\n" +
                "                            </p>\n" +
                "                            <p style=\"margin: 0 0 30px; color: #333333; font-size: 16px; line-height: 1.6;\">\n" +
                "                                Please take a moment to rate your ride by clicking the button below:\n" +
                "                            </p>\n" +
                "                            <table role=\"presentation\" style=\"margin: 0 auto;\">\n" +
                "                                <tr>\n" +
                "                                    <td style=\"border-radius: 6px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\">\n" +
                "                                        <a href=\"" + ratingLink + "\" style=\"display: inline-block; padding: 16px 40px; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: 600; border-radius: 6px;\">\n" +
                "                                            Rate your ride ⭐\n" +
                "                                        </a>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                            <p style=\"margin: 30px 0 0; color: #666666; font-size: 14px; line-height: 1.6;\">\n" +
                "                                Your feedback helps us improve our service.\n" +
                "                            </p>\n" +
                "                            <p style=\"margin: 30px 0 0; color: #666666; font-size: 14px; line-height: 1.6;\">\n" +
                "                                If the button doesn't work, copy and paste this link into your browser:\n" +
                "                            </p>\n" +
                "                            <p style=\"margin: 10px 0 0; color: #667eea; font-size: 14px; word-break: break-all;\">\n" +
                "                                " + ratingLink + "\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "</html>";

        String subject = "Your ride is finished – rate your experience 🚗⭐";

        try {
            sendMail(email, subject, body);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendEmailWhenPassengerIsAddedToRide(String email, String token){
        String trackingLink = "http://localhost:4200/tracking-route/"+token;
        String body = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Ride Finished</title>\n" +
                "</head>\n" +
                "<body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;\">\n" +
                "    <table role=\"presentation\" style=\"width: 100%; border-collapse: collapse;\">\n" +
                "        <tr>\n" +
                "            <td align=\"center\" style=\"padding: 40px 0;\">\n" +
                "                <table role=\"presentation\" style=\"width: 600px; border-collapse: collapse; background-color: #ffffff; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\">\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 40px 30px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); text-align: center;\">\n" +
                "                            <h1 style=\"margin: 0; color: #ffffff; font-size: 28px; font-weight: 600;\">You are added to a ride 🚗</h1>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                    <tr>\n" +
                "                        <td style=\"padding: 40px 30px;\">\n" +
                "                            <p style=\"margin: 0 0 20px; color: #333333; font-size: 16px; line-height: 1.6;\">Hello!</p>\n" +
                "                            <p style=\"margin: 0 0 30px; color: #333333; font-size: 16px; line-height: 1.6;\">\n" +
                "                                You can track your ride by clicking on a button below!\n" +
                "                            <table role=\"presentation\" style=\"margin: 0 auto;\">\n" +
                "                                <tr>\n" +
                "                                    <td style=\"border-radius: 6px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);\">\n" +
                "                                        <a href=\"" + trackingLink + "\" style=\"display: inline-block; padding: 16px 40px; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: 600; border-radius: 6px;\">\n" +
                "                                            Track your ride ⭐\n" +
                "                                        </a>\n" +
                "                                    </td>\n" +
                "                                </tr>\n" +
                "                            </table>\n" +
                "                            <p style=\"margin: 30px 0 0; color: #666666; font-size: 14px; line-height: 1.6;\">\n" +
                "                                If the button doesn't work, copy and paste this link into your browser:\n" +
                "                            </p>\n" +
                "                            <p style=\"margin: 10px 0 0; color: #667eea; font-size: 14px; word-break: break-all;\">\n" +
                "                                " + trackingLink + "\n" +
                "                            </p>\n" +
                "                        </td>\n" +
                "                    </tr>\n" +
                "                </table>\n" +
                "            </td>\n" +
                "        </tr>\n" +
                "    </table>\n" +
                "</body>\n" +
                "</html>";

        String subject = "We have a new ride for you 🚗⭐";

        try {
            sendMail(email, subject, body);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
