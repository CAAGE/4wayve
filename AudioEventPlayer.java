import java.util.ArrayList;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Receiver;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.InvalidMidiDataException;
import java.io.File;

public class AudioEventPlayer {
	
	public static int MAXIMPLEMENTEDINSTRUMENTINDEX = 3;
	
	public static void startNote(int instrument, int note)  throws MidiUnavailableException,InvalidMidiDataException {
        Receiver rcvr = MidiSystem.getReceiver();
        int notevalue = 0;
        int bank = 0;
        if(instrument == 0){
            bank = 0;
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
        if(instrument == 1){
            bank = 9;
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,0,0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 36;
            }
            if(note == 1){
                notevalue = 38;
            }
            if(note == 2){
                notevalue = 49;
            }
        }
        if(instrument == 2){
            bank = 0;
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,33,0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 36;
            }
            if(note == 1){
                notevalue = 40;
            }
            if(note == 2){
                notevalue = 43;
            }
        }
        if(instrument == 3){
            bank = 0;
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,26,0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 48;
            }
            if(note == 1){
                notevalue = 52;
            }
            if(note == 2){
                notevalue = 55;
            }
        }
        if(instrument == -1){
            bank = 0;
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 115, 0);
            rcvr.send(midiinstrument,-1);
            notevalue = 60;
        }
        ShortMessage midinote = new ShortMessage();
        midinote.setMessage(ShortMessage.NOTE_ON, bank, notevalue, 93);
        rcvr.send(midinote,-1);
                   
	}

	public static void endNote(int instrument, int note) throws MidiUnavailableException,InvalidMidiDataException {
        Receiver rcvr = MidiSystem.getReceiver();
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
        if(instrument == 2){
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,33,0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 36;
            }
            if(note == 1){
                notevalue = 40;
            }
            if(note == 2){
                notevalue = 43;
            }
        }
        if(instrument == 3){
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,26,0);
            rcvr.send(midiinstrument,-1);
            if(note == 0){
                notevalue = 48;
            }
            if(note == 1){
                notevalue = 52;
            }
            if(note == 2){
                notevalue = 55;
            }
        }
        if(instrument == -1){
            ShortMessage midiinstrument = new ShortMessage();
            midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 115, 0);
            rcvr.send(midiinstrument,-1);
            notevalue = 60;
        }
        ShortMessage midinote = new ShortMessage();
	    midinote.setMessage(ShortMessage.NOTE_OFF,0,notevalue,93);
        rcvr.send(midinote,-1);
        
        } catch (Exception e) {}
	}
	
	/**
	 * This will write events to a file.
	 * @param location The file to write to.
	 * @param instruments The instruments in the file.
	 * @param startTimes The start times of each event.
	 * @param endTimes The end times of each instrument event.
	 * @throws IOException If there is a problem writing.
	 */
	public static void saveEventFile(File location, int[] instruments, List<List<Long>> startTimes, List<List<Long>> endTimes) throws IOException{
		DataOutputStream toWrite = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(location)));
		toWrite.writeInt(instruments[0]);
		toWrite.writeInt(instruments[1]);
		toWrite.writeInt(instruments[2]);
		toWrite.writeInt(instruments[3]);
		for(int i = 0; i<12; i++){
			List<Long> stimes = startTimes.get(i);
			List<Long> etimes = endTimes.get(i);
			toWrite.writeInt(stimes.size());
			for(Long cur : stimes){
				toWrite.writeLong(cur);
			}
			toWrite.writeInt(etimes.size());
			for(Long cur : etimes){
				toWrite.writeLong(cur);
			}
		}
		toWrite.close();
	}
	
	/**
	 * This will read events from a file.
	 * @param location The file to read from.
	 * @return {instruments, startTimes, endTimes}.
	 * @throws IOException If there is a problem reading.
	 */
	public static Object[] readEventFile(File location) throws IOException{
		DataInputStream toRead = new DataInputStream(new BufferedInputStream(new FileInputStream(location)));
		int[] instruments = new int[4];
		instruments[0] = toRead.readInt();
		instruments[1] = toRead.readInt();
		instruments[2] = toRead.readInt();
		instruments[3] = toRead.readInt();
		List<List<Long>> startTimes = new ArrayList<>();
		List<List<Long>> endTimes = new ArrayList<>();
		for(int i = 0; i<12; i++){
			List<Long> stimes = new ArrayList<>();
			List<Long> etimes = new ArrayList<>();
			int slen = toRead.readInt();
			for(int j = 0; j<slen; j++){
				stimes.add(toRead.readLong());
			}
			int elen = toRead.readInt();
			for(int j = 0; j<elen; j++){
				etimes.add(toRead.readLong());
			}
			startTimes.add(stimes);
			endTimes.add(etimes);
		}
		toRead.close();
		return new Object[]{instruments, startTimes, endTimes};
	}
	public static int generateMidiFile(File fileToWrite, int[] instrumentList, List<List<Long>> startTimes, List<List<Long>> endTimes) throws InvalidMidiDataException,IOException{
        Sequence s = new Sequence(javax.sound.midi.Sequence.PPQ,24);
        Track t = s.createTrack();
        int header[] = new int[]{
        0x4d, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06,
        0x00, 0x00, // single-track format
        0x00, 0x01, // one track
        0x00, 0x10, // 16 ticks per quarter
        0x4d, 0x54, 0x72, 0x6B
        };
        MetaMessage mmm = new MetaMessage();
        MidiEvent me = new MidiEvent(mmm,(long)0);
        //use general midi
        byte[] b = {(byte)0xF0, 0x7E, 0x7F, 0x09, 0x01, (byte)0xF7};
		SysexMessage sm = new SysexMessage();
		sm.setMessage(b, 6);
		me = new MidiEvent(sm,(long)0);
		t.add(me);
        //set tempo
        MetaMessage mt = new MetaMessage();
        byte[] bt = {0x02, (byte)60, 0x00};
		mt.setMessage(0x51 ,bt, 3);
		me = new MidiEvent(mt,(long)0);
		t.add(me);
        //set omni on
        ShortMessage mm = new ShortMessage();
		mm.setMessage(0xB0, 0x7D,0x00);
		me = new MidiEvent(mm,(long)0);
		t.add(me);
        //set poly on
        mm = new ShortMessage();
		mm.setMessage(0xB0, 0x7F,0x00);
		me = new MidiEvent(mm,(long)0);
		t.add(me);
        int instrument = 0;
        int bank = 0;
        int note = 0;
        int notevalue = 0;
        long lastframe = 0;
        for(int lane = 0;lane<12;lane++){
            if(0<=lane && lane<= 2){
                instrument = instrumentList[0];
            }
            if(3<=lane && lane<=5){
                instrument = instrumentList[1];
            }
            if(6<=lane && lane<=8){
                instrument = instrumentList[2];
            }
            if(9<=lane){
                instrument = instrumentList[3];
            }
            note = lane %3;
            int eventcount = startTimes.get(lane).size();
            for(int i = 0;i<eventcount;i++){
                if(instrument == 0){
                    bank = 0;
                    ShortMessage midiinstrument = new ShortMessage();
                    midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 0, 0);
                    me = new MidiEvent(midiinstrument,(long)startTimes.get(lane).get(i)-1);
                    t.add(me);
                    if(note == 0){
                        notevalue = 60;
                    } if(note == 1){
                        notevalue = 64;
                    } if(note == 2){
                        notevalue = 67;
                    }
                }
                if(instrument == 1){
                    bank = 9;
                    ShortMessage midiinstrument = new ShortMessage();
                    midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,0,0);
                    me = new MidiEvent(midiinstrument,(long)startTimes.get(lane).get(i)-1);
                    t.add(me);
                    if(note == 0){
                        notevalue = 36;
                    }
                    if(note == 1){
                        notevalue = 38;
                    }
                    if(note == 2){
                        notevalue = 49;
                    }
                }
                if(instrument == 2){
                    bank = 0;
                    ShortMessage midiinstrument = new ShortMessage();
                    midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,33,0);
                    me = new MidiEvent(midiinstrument,(long)startTimes.get(lane).get(i)-1);
                    t.add(me);
                    if(note == 0){
                        notevalue = 36;
                    }
                    if(note == 1){
                        notevalue = 40;
                    }
                    if(note == 2){
                        notevalue = 43;
                    }
                }
                if(instrument == 3){
                    bank = 0;
                    ShortMessage midiinstrument = new ShortMessage();
                    midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,26,0);
                    me = new MidiEvent(midiinstrument,(long)startTimes.get(lane).get(i)-1);
                    t.add(me);
                    if(note == 0){
                        notevalue = 48;
                    }
                    if(note == 1){
                        notevalue = 52;
                    }
                    if(note == 2){
                        notevalue = 55;
                    }
                }
                ShortMessage midinote = new ShortMessage();
                midinote.setMessage(ShortMessage.NOTE_ON, bank, notevalue, 93);
                me = new MidiEvent(midinote,(long)startTimes.get(lane).get(i));
                t.add(me);
                if((long)startTimes.get(lane).get(i)>lastframe){
                    lastframe = startTimes.get(lane).get(i);
                }
            }
        }
        for(int lane = 0;lane<12;lane++){
            if(0<=lane && lane<= 2){
                instrument = instrumentList[0];
            }
            if(3<=lane && lane<=5){
                instrument = instrumentList[1];
            }
            if(6<=lane && lane<=8){
                instrument = instrumentList[2];
            }
            if(9<=lane){
                instrument = instrumentList[3];
            }
            note = lane %3;
            int eventcount = endTimes.get(lane).size();
            for(int i = 0;i<eventcount;i++){
                if(instrument == 0){
                    bank = 0;
                    ShortMessage midiinstrument = new ShortMessage();
                    midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 0, 0);
                    me = new MidiEvent(midiinstrument,(long)endTimes.get(lane).get(i)-1);
                    t.add(me);
                    if(note == 0){
                        notevalue = 60;
                    } if(note == 1){
                        notevalue = 64;
                    } if(note == 2){
                        notevalue = 67;
                    }
                }
                if(instrument == 1){
                    bank = 9;
                    ShortMessage midiinstrument = new ShortMessage();
                    midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,0,0);
                    me = new MidiEvent(midiinstrument,(long)endTimes.get(lane).get(i)-1);
                    t.add(me);
                    if(note == 0){
                        notevalue = 36;
                    }
                    if(note == 1){
                        notevalue = 38;
                    }
                    if(note == 2){
                        notevalue = 49;
                    }
                }
                if(instrument == 2){
                    bank = 0;
                    ShortMessage midiinstrument = new ShortMessage();
                    midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,33,0);
                    me = new MidiEvent(midiinstrument,(long)endTimes.get(lane).get(i)-1);
                    t.add(me);
                    if(note == 0){
                        notevalue = 36;
                    }
                    if(note == 1){
                        notevalue = 40;
                    }
                    if(note == 2){
                        notevalue = 43;
                    }
                }
                if(instrument == 3){
                    bank = 0;
                    ShortMessage midiinstrument = new ShortMessage();
                    midiinstrument.setMessage(ShortMessage.PROGRAM_CHANGE,0,26,0);
                    me = new MidiEvent(midiinstrument,(long)endTimes.get(lane).get(i)-1);
                    t.add(me);
                    if(note == 0){
                        notevalue = 48;
                    }
                    if(note == 1){
                        notevalue = 52;
                    }
                    if(note == 2){
                        notevalue = 55;
                    }
                }
                ShortMessage midinote = new ShortMessage();
                midinote.setMessage(ShortMessage.NOTE_OFF, bank, notevalue, 93);
                me = new MidiEvent(midinote,(long)endTimes.get(lane).get(i)*3);
                t.add(me);
                if((long)endTimes.get(lane).get(i)>lastframe){
                    lastframe = endTimes.get(lane).get(i)*3;
                }
            }
        }
        System.out.println(lastframe);
        //end file
        mt = new MetaMessage();
        byte[] bet = {}; // empty array
		mt.setMessage(0x2F,bet,0);
		me = new MidiEvent(mt, (long)lastframe+1);
		t.add(me);
        //output to file
		MidiSystem.write(s,1,fileToWrite);
        return 0;
    }
    
	public static void main(String[] args) throws MidiUnavailableException,InvalidMidiDataException,IOException{
        Sequencer sequencer;
	    // Get default sequencer.
	    sequencer = MidiSystem.getSequencer(); 
	    if (sequencer == null) {
	        // Error -- sequencer device is not supported.
	        // Inform user and return...	    	
	    } else {
	        // Acquire resources and make operational.
            sequencer.open();
            startNote(3,0);
		    try{Thread.sleep(500);}catch(InterruptedException e){}
            endNote(3,0);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            startNote(0,1);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            endNote(0,1);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            startNote(1,2);
            try{Thread.sleep(500);}catch(InterruptedException e){}
            endNote(1,2);
            //generateMidiFile();
        }   
    }
}
