/*Created By Siddharth Somvanshi
    Started At 24DEC 2017 -- 9:30pm
 */
//packages and imported java classes
package sample;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Slider;
import javafx.scene.control.Button;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import java.io.*;
import java.net.URL;
import java.util.*;

import javafx.collections.MapChangeListener;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.scene.control.ListView;
import javafx.util.Duration;

//---------------------------------------------------------------------------------------------------------------
//Main Controller Class Starts ----------------------------------------------------------------------------------------------------------

public class Controller implements Initializable {

    //Media Player Configration Starts --------------------------------------------------------------------------------------------------
    //Media Player Variable
    @FXML
    AnchorPane player = new AnchorPane();
    @FXML
    TextArea textarea;
    @FXML
    ImageView albumart = new ImageView();
    @FXML
    ImageView playera = new ImageView();
    @FXML
    ImageView playerb = new ImageView();
    @FXML
    Label volumetxt = new Label();
    @FXML
    Label name = new Label();
    @FXML
    Label artistn = new Label();
    @FXML
    Label currenttime = new Label();
    @FXML
    Label totaltime = new Label();
    @FXML
    Slider seek = new Slider();
    @FXML
    Slider volsli = new Slider();
    @FXML
    Button playpause = new Button();
    File[] array;
    public List<File> selectedFiles = Stream.of(new File[]{}).collect(Collectors.toList());
    public Iterator<File> itr;
    @FXML
    public ListView playlist;
    @FXML
    public MediaView mv;
    public MediaPlayer mp;
    public Media me;
    public int statevalue = 1;
    public int currentindex;
    public double volume=1;
    @FXML
    private WebView webview;
    private WebEngine engine;
    public double time2;

    //Media Player Methods ---------------------------------------------------------------------------------------------------------------

    public void addmusic() {
        //this method is responsible for loading and removing song in listview and playing list
        //removing all elemnts from the list making it empty
        if(selectedFiles.size()!=0) {
            Collection c = new ArrayList(Arrays.asList(selectedFiles));
            c.removeAll(selectedFiles);
        }
        //declaring multiple file chooser
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator")+ "Music"));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Music Files -- MP3", "*.mp3"));
        //storing files in list
        selectedFiles = fc.showOpenMultipleDialog(null);
        //clearing viewlist
        playlist.getItems().clear();
        //checking whether list is empty or not if not then loading items in listview
        if (selectedFiles != null) {
            for (int i = 0; i < selectedFiles.size(); i++) {
                playlist.getItems().add(selectedFiles.get(i).getName());
            }
        }
        //creating meadiagroups
        final Group mediagroup = new Group();
        //creating list iterator
        itr = selectedFiles.iterator();
        //loading player with first file
        loadplayer(selectedFiles.get(0).toURI().toString());
        //adding mediaviews in group
        mediagroup.getChildren().add(mv);
        String currentmedia = me.getSource().toString();
        //here happens the magic of current index
        loop1:for (int j = 0; j <= selectedFiles.size(); j++) {
            if (currentmedia.equals(selectedFiles.get(j).toURI().toString())) {
                currentindex = j;
                break loop1;
            }
        }
    }

    //---------------------------------------------------------------------------------------------------------------

    public void loadplayer(String path) {
        //default player initializations
        Image img = new Image("sample/res/default.jpg");
        albumart.setImage(img);
        playera.setImage(img);
        playerb.setImage(img);
        name.setText("Unknown");
        artistn.setText("Unknown");
        if (path == null)
            return;
        if (mp != null) {
            mp.stop();
            mp = null;
            me = null;
            seek.setValue(0);
            statevalue = 1;
            String pic = "sample/res/pause.png";
            playpause.setStyle("-fx-background-image: url('"+pic+"');");
        }
        me = new Media(path);
        mp = new MediaPlayer(me);
        mp.setOnReady(new Runnable() {
            public void run() {
                currenttime.setText("0.00");
                double time2 = me.getDuration().toSeconds();
                double lasttime = me.getDuration().toMinutes();
                String duration2 = new Double(lasttime).toString();
                String duration = new Double(time2).toString();
                totaltime.setText(duration2.substring(0, 4));
                seek.setMax(time2);
                seek.valueProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(javafx.beans.Observable observable) {
                        System.out.println(seek.getValue());
                        System.out.println(me.getDuration().toSeconds());
                        mp.seek(Duration.seconds(seek.getValue()));

                        double time1 = mp.getCurrentTime().toMinutes();
                        String duration = new Double(time1).toString();
                        currenttime.setText(duration.substring(0,4));

                    }
                });
            }
        });
        mp.setAutoPlay(true);
        volsli.setMax(100);
        mp.setVolume(volume);
        volsli.setValue(volume*100);
        if (volume == 1 || volume*100 == 1) {
            volumetxt.setText("100");
        }else if (volume < 1) {
            volumetxt.setText(String.valueOf(volume).substring(2,4));
        }
        else if (volume < 0.1) {
            volumetxt.setText(String.valueOf(volume).substring(2,3));
        }
        System.out.println(currentindex);
        System.out.println(selectedFiles.size());
        volsli.valueProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                 double val = volsli.getValue();
                 volume = val / 100;
                 mp.setVolume(val / 100);
                if (volume == 1 || volume == 1.0) {
                    volumetxt.setText("100");
                } else if (volume < 1) {
                    volumetxt.setText(String.valueOf(volume*100).substring(0,2));
                }else if (volume < 0.1) {
                    volumetxt.setText(String.valueOf(volume*100).substring(0,0));
                }
            }
        });
        try {
            me.getMetadata().addListener(new MapChangeListener<String, Object>() {
                @Override
                public void onChanged(Change<? extends String, ? extends Object> ch) {
                    if (ch.wasAdded()) {
                        handleMetadata(ch.getKey(), ch.getValueAdded());
                    }
                }

                public void handleMetadata(String key, Object value) {
                    if (key.equals("title")) {
                        name.setText("Unknown...");
                        String title = value.toString();
                        name.setText(title);
                    }
                    if (key.equals("artist")) {
                        artistn.setText("Unknown Artist");
                        String artist = "Artist: "+value.toString();
                        artistn.setText(artist);
                    }
                    if (key.equals("image")) {
                        albumart.setImage((Image) value);
                        playera.setImage((Image) value);
                        playerb.setImage((Image) value);
                    }
                }
            });
        }
        catch(RuntimeException re) {
            // Handle construction errors
            System.out.println("Caught Exception: " + re.getMessage());
        }
        //this function works when one media finished or stopped playing
        mp.setOnEndOfMedia(new Runnable() {
            //runnable run() method run automatically
            @Override
            public void run() {
                if (currentindex == 0) {
                    //Plays the subsequent files
                    currentindex = currentindex + 1;
                    loadplayer(selectedFiles.get(currentindex).toURI().toString());
                    System.out.println(selectedFiles.size());
                    System.out.println(currentindex);

                } else if (currentindex == selectedFiles.size()-1) { //this elseif checks whether the file is last or not if disturbed
                    loadplayer(selectedFiles.get(0).toURI().toString());
                    //updating the value
                    currentindex = 0;
                } else if (selectedFiles.size() == 2 & currentindex == 1) { // this else block move to next media from list if it is disturbed by user and not last file
                    loadplayer(selectedFiles.get(0).toURI().toString());
                    currentindex = 0;
                }else {
                    String currentmedia = selectedFiles.get(currentindex+1).toURI().toString();
                    loadplayer(currentmedia);
                    loop1:for (int j = 0; j <= selectedFiles.size(); j++) {
                        if (currentmedia.equals(selectedFiles.get(j).toURI().toString())) {
                            currentindex = j;
                            break loop1;
                        }
                    }
                    }
                }
        });
    }

    //---------------------------------------------------------------------------------------------------------------


    public void playpause() {
        if(statevalue==1){
            statevalue = 0;
            mp.pause();
            String pic = "sample/res/play.png";
            playpause.setStyle("-fx-background-image: url('"+pic+"');");
        }else if(statevalue==0){
            statevalue = 1;
            mp.play();
            String pic = "sample/res/pause.png";
            playpause.setStyle("-fx-background-image: url('"+pic+"');");
        }else if(statevalue==3){
            statevalue = 1;
            String pic = "sample/res/pause.png";
            playpause.setStyle("-fx-background-image: url('"+pic+"');");
        }
    }

    //---------------------------------------------------------------------------------------------------------------


    public void next(){
        //this two things removes the current media from the player
        mp.stop();
        //as requested next song this make song index to next song
        if(currentindex==0) {
            currentindex=currentindex+1;
        }else if(currentindex==selectedFiles.size()-1){
            currentindex = 0;
        }else{
            currentindex = currentindex+1;
        }
        //checking whether it is last song or not
            String currentmedia = selectedFiles.get(currentindex).toURI().toString();
            loadplayer(currentmedia);
            loop1:for (int j = 0; j <= selectedFiles.size(); j++) {
                if (currentmedia.equals(selectedFiles.get(j).toURI().toString())) {
                    currentindex = j;
                    break loop1;
                }
            }
            //if it is last song move plaer towars the first song and set index value to 0 i.e fist song
    }


    //---------------------------------------------------------------------------------------------------------------


    public void previous() {
        //this two things removes the current media from the player
        mp.stop();
        me = null;
        //as previous requested getting the index of previous song
        int previousmedia=currentindex-1;
        //checking for whether this fisrst song or not if yes then restart fom firts using itreator
        if(previousmedia<0){
            itr = selectedFiles.iterator();
            loadplayer(itr.next().toURI().toString());
        }
        //if this is not fist song using previous index load the previous song
        loadplayer(selectedFiles.get((previousmedia)).toURI().toString());
        currentindex = currentindex-1;
        itr.equals(selectedFiles.get(currentindex).toURI().toString());
    }

    //---------------------------------------------------------------------------------------------------------------


    public void playlistsave() {

    }

    // File Systems -----------------------------------------------------------------------------------

    public void save() throws FileNotFoundException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(System.getProperty("user.home") + System.getProperty("file.separator")+ "Music"));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File", "*.txt"));
        fc.setTitle("Save File");
        File name = fc.showSaveDialog(null);
        if(name!=null){
            try{
                String content = textarea.getText();
                BufferedWriter bw = new BufferedWriter(new FileWriter(name.getAbsoluteFile()));
                String lines[] = content.split("\\r?\\n");
                System.out.println(lines[1]);
                for (String line : lines){
                    bw.write(line);
                    bw.write(System.lineSeparator());
                }
                bw.close();



            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    //Auto Runner ---------------------------------------------------------------------------------------------------------------


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //this function runs at prongram startup like args method
        engine = webview.getEngine();
        engine.setJavaScriptEnabled(true);
        //making mobile friendly
        engine.setUserAgent("Mozilla/55.0 (iphone; u; CPU like Mac OS X; en) Applewebkit/420+ (KHTML, like Gecko)");
        //selected page loader with enabled javascript
        engine.load("https://m.facebook.com");
        BoxBlur blur = new BoxBlur();
        blur.setHeight(100);
        blur.setWidth(10);
        blur.setIterations(100);
        playerb.setEffect(blur);
        playera.setEffect(blur);

    }
}
