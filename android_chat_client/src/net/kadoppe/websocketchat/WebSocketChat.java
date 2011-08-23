package net.kadoppe.websocketchat;

import java.net.URI;
import java.net.URISyntaxException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import de.roderick.weberknecht.WebSocket;
import de.roderick.weberknecht.WebSocketConnection;
import de.roderick.weberknecht.WebSocketEventHandler;
import de.roderick.weberknecht.WebSocketException;
import de.roderick.weberknecht.WebSocketMessage;

public class WebSocketChat extends Activity implements View.OnClickListener{
    
	private static String SERVER_HOST = "10.0.2.1";
	
	private WebSocket websocket;
	private EditText messageEditText;
	private TextView logsTextView;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    
        messageEditText = (EditText)this.findViewById(R.id.messageEditText);
        logsTextView = (TextView)this.findViewById(R.id.logsTextView);
        
        try {
        	URI url  = new URI("ws://" + SERVER_HOST + "/");
        	websocket = new WebSocketConnection(url);
        	
        	websocket.setEventHandler(new WebSocketEventHandler() {
        		Handler handler = new Handler();
        		
        		public void onOpen() {
        			updateLog("connected.");
        		}
        		
        		public void onMessage(final WebSocketMessage message) {
        			handler.post(new Runnable() {
        				public void run() {
        					updateLog(message.getText().trim());
        				}
        			});
        		}
        		
        		public void onClose() {
        			updateLog("closed");
        		}
        	});
        	
        	View sendButton = this.findViewById(R.id.sendButton);
            sendButton.setOnClickListener(this);
            
        } catch (WebSocketException wse) {
        	wse.printStackTrace();
        } catch (URISyntaxException use) {
        	use.printStackTrace();
        }
    } 
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    	try {
    		websocket.connect();
    	} catch (WebSocketException wse) {
    		wse.printStackTrace();
    	}
    }
    
    public void onClick(View view) {
    	try {
    		websocket.send(messageEditText.getText().toString());
    		messageEditText.setText("");
    	} catch (WebSocketException wse) {
    		wse.printStackTrace();
    	}
    }
   
    private void updateLog(String message) {
    	logsTextView.setText("> " + message + "\n" + logsTextView.getText());
    }
}