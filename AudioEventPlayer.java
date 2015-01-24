import javax.sound.midi.Synthesizer;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;

import java.io.File;
public class AudioEventPlayer {
	public static void startNote(int instrument, int note)  throws MidiUnavailableException {
		Sequencer sequencer;
	    // Get default sequencer.
	    sequencer = MidiSystem.getSequencer(); 
	    if (sequencer == null) {
	        // Error -- sequencer device is not supported.
	        // Inform user and return...
	    	
	    } else {
	         // Acquire resources and make operational.
	    	try {
	            ShortMessage pianoc = new ShortMessage();
	            pianoc.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
	            ShortMessage pianoend = new ShortMessage();
	            pianoend.setMessage(ShortMessage.NOTE_OFF,0,60,93);
	            Receiver rcvr = MidiSystem.getReceiver();
	            rcvr.send(pianoc,-1);
				try{Thread.sleep(500);}catch(InterruptedException e){}
	            rcvr.send(pianoend,-1);
	            sequencer.open();
	        } catch (Exception e) {
	            // Handle error and/or return
	        }
	    }
	}
	public static void endNote(int instrument, int note) {
		
	}
	public static void main(String[] args) throws MidiUnavailableException{
		startNote(0,0);
	}
}