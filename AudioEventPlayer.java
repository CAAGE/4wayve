import javax.sound.midi.Synthesizer;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.InvalidMidiDataException;

import java.io.File;

public class AudioEventPlayer {
	public static void startNote(int instrument, int note)  throws MidiUnavailableException,InvalidMidiDataException {
        Receiver rcvr = MidiSystem.getReceiver();
        int notevalue = 0;
        if(instrument == 0){
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 0, 0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 60;
            } if(note == 1){
                notevalue = 64;
            } if(note == 2){
                notevalue = 67;
            }
        }
        if(instrument == -1){
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 115, 0);
            rcvr.send(midiinstrument,-1);
            notevalue = 60;
        ShortMessage midinote = new ShortMessage();
        midinote.setMessage(ShortMessage.NOTE_ON, 0, notevalue, 93);
        rcvr.send(midinote,-1);
        }           
	}
	public static void endNote(int instrument, int note) throws MidiUnavailableException,InvalidMidiDataException {
        try{
        int notevalue = 0;
        if(instrument == 0){
            if(note == 0){
                notevalue = 60;
            } if(note == 1){
                notevalue = 64;
            } if(note == 2){
                notevalue = 67;
            }
        }
        Receiver rcvr = MidiSystem.getReceiver();
        ShortMessage midinote = new ShortMessage();
	    midinote.setMessage(ShortMessage.NOTE_OFF,instrument,notevalue,93);
        rcvr.send(midinote,-1);
        } catch (Exception e) {}
	}
	public static void main(String[] args) throws MidiUnavailableException,InvalidMidiDataException{
        Sequencer sequencer;
	    // Get default sequencer.
	    sequencer = MidiSystem.getSequencer(); 
	    if (sequencer == null) {
	        // Error -- sequencer device is not supported.
	        // Inform user and return...	    	
	    } else {
	        // Acquire resources and make operational.
            sequencer.open();
            startNote(-1,0);
		    try{Thread.sleep(500);}catch(InterruptedException e){}
            endNote(-1,0);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            startNote(0,1);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            endNote(0,1);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            startNote(0,2);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            endNote(0,2);
        }   
    }
}
