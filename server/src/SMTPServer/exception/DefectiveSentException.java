package SMTPServer.exception;

public class DefectiveSentException extends Exception {
    public DefectiveSentException(final String message){
        super(message);
    }
}
