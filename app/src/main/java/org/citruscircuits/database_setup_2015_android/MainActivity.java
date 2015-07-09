package org.citruscircuits.database_setup_2015_android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import org.citruscircuits.database_setup_2015_android.realm.CalculatedCompetitionData;
import org.citruscircuits.database_setup_2015_android.realm.CalculatedMatchData;
import org.citruscircuits.database_setup_2015_android.realm.CalculatedTeamData;
import org.citruscircuits.database_setup_2015_android.realm.CalculatedTeamInMatchData;
import org.citruscircuits.database_setup_2015_android.realm.Competition;
import org.citruscircuits.database_setup_2015_android.realm.Match;
import org.citruscircuits.database_setup_2015_android.realm.Team;
import org.citruscircuits.database_setup_2015_android.realm.TeamInMatchData;
import org.citruscircuits.database_setup_2015_android.realm.UploadedTeamData;
import org.citruscircuits.database_setup_2015_android.realm.UploadedTeamInMatchData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;

public class MainActivity extends ActionBarActivity {
    DbxAccountManager mDbxAcctMgr;
    DbxFileSystem dbxFs;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupDbx();

        ImageButton createDatabaseImageButton = (ImageButton)findViewById(R.id.button);

        createDatabaseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDatabase();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setupDbx() {
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), Utils.APP_KEY, Utils.APP_SECRET);

        if(!mDbxAcctMgr.hasLinkedAccount()) {
            mDbxAcctMgr.startLink(this, Utils.REQUEST_LINK_TO_DBX);
        } else {
            setupDbxFs();
        }
    }

    public void setupDbxFs() {
        try {
            dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
        } catch (DbxException dbxe) {
            Utils.handleError(dbxe);
        }
    }

    public JSONObject getJSONSchedule() {
        try {
            Log.e("dasf", dbxFs.toString());
            DbxFile scheduleFile = dbxFs.open(new DbxPath(Utils.SCHEDULE_PATH));

            JSONObject scheduleArray = new JSONObject(scheduleFile.readString());

            scheduleFile.close();

            Log.e("test", "Did we get here?");
            return scheduleArray;
        } catch (DbxException dbxe) {
            Utils.handleError(dbxe);
        } catch (IOException ioe) {
            Utils.handleError(ioe);
        } catch (JSONException jsone) {
            Utils.handleError(jsone);
        }

        return null;
    }

    public void createDatabase() {
        realm = Realm.getInstance(this, Utils.FILE_NAME);

//        JSONObject schedule = getJSONSchedule();

//        Log.e("test", schedule.toString());

        createMatches(null);

        uploadDbFile(Utils.FILE_NAME);
    }

    public void createMatches(JSONObject schedule) {
//        try {
            realm.beginTransaction();

            Competition competition = realm.createObject(Competition.class);
            competition.setName("Champs");
//            Log.e("test", (String)schedule.get("compCode"));

            CalculatedCompetitionData calculatedCompetitionData = realm.createObject(CalculatedCompetitionData.class);
            competition.setCalculatedData(calculatedCompetitionData);

//            JSONArray matches = schedule.getJSONArray("matches");
//
//            for (int i = 0; i < matches.length(); i++) {
//                JSONObject jsonMatch = (JSONObject)matches.get(i);
//
//                Match match = realm.createObject(Match.class);
//                match.setMatch(jsonMatch.getString("name"));
//                match.setOfficialRedScore(-1);
//                match.setOfficialBlueScore(-1);
//
//                CalculatedMatchData calculatedMatchData = realm.createObject(CalculatedMatchData.class);
//                match.setCalculatedData(calculatedMatchData );
//
//                competition.getMatches().add(match);
//
//                JSONArray jsonRedAlliance = jsonMatch.getJSONArray("redAlliance");
//
//                for (int r = 0; r < jsonRedAlliance.length(); r++) {
//                    JSONObject jsonTeam = jsonRedAlliance.getJSONObject(r);
//
//                    int number = Integer.parseInt(Utils.formatForDatabase((String)jsonTeam.get("number")));
//
//                    RealmQuery<Team> teamsQuery = realm.where(Team.class);
//                    teamsQuery.equalTo("number", number);
//
//                    Team team = teamsQuery.findFirst();
//
//                    if (team == null) {
//                        team = realm.createObject(Team.class);
//                        team.setNumber(number);
//
//                        try {
//                            team.setName((String)jsonTeam.get("name"));
//                        } catch (ClassCastException cce) {
//                            team.setName(Utils.generateRandomTeamName());
//                        }
//
//                        UploadedTeamData uploadedTeamData = realm.createObject(UploadedTeamData.class);
//                        team.setUploadedData(uploadedTeamData);
//
//                        CalculatedTeamData calculatedTeamData = realm.createObject(CalculatedTeamData.class);
//                        team.setCalculatedData(calculatedTeamData);
//                    }
//
//                    TeamInMatchData teamInMatchData = realm.createObject(TeamInMatchData.class);
//                    teamInMatchData.setMatch(match);
//                    teamInMatchData.setTeam(team);
//
//                    UploadedTeamInMatchData uploadedTeamInMatchData = realm.createObject(UploadedTeamInMatchData.class);
//                    uploadedTeamInMatchData.setMaxFieldToteHeight(-1);
//                    teamInMatchData.setUploadedData(uploadedTeamInMatchData);
//
//                    CalculatedTeamInMatchData calculatedTeamInMatchData = realm.createObject(CalculatedTeamInMatchData.class);
//                    teamInMatchData.setCalculatedData(calculatedTeamInMatchData);
//                    Log.e("test", team.toString());
//                    if (team.getMatchData() == null) {
//                        RealmList<TeamInMatchData> teamInMatchDatas = new RealmList<TeamInMatchData>();
//                        team.setMatchData(teamInMatchDatas);
//                    }
//
//                    team.getMatchData().add(teamInMatchData);
//
//                    match.getRedTeams().add(team);
//                    match.getTeamInMatchDatas().add(teamInMatchData);
//                }
//
//                JSONArray jsonBlueAlliance = jsonMatch.getJSONArray("blueAlliance");
//                for (int r = 0; r < jsonBlueAlliance.length(); r++) {
//                    JSONObject jsonTeam = jsonBlueAlliance.getJSONObject(r);
//
//                    int number = Integer.parseInt(Utils.formatForDatabase((String)jsonTeam.get("number")));
//
//                    RealmQuery<Team> teamsQuery = realm.where(Team.class);
//                    teamsQuery.equalTo("number", number);
//
//                    Team team = teamsQuery.findFirst();
//
//                    if (team == null) {
//                        team = realm.createObject(Team.class);
//                        team.setNumber(number);
//
//                        try {
//                            team.setName((String)jsonTeam.get("name"));
//                        } catch (ClassCastException cce) {
//                            team.setName(Utils.generateRandomTeamName());
//                        }
//
//                        UploadedTeamData uploadedTeamData = realm.createObject(UploadedTeamData.class);
//                        team.setUploadedData(uploadedTeamData);
//
//                        CalculatedTeamData calculatedTeamData = realm.createObject(CalculatedTeamData.class);
//                        team.setCalculatedData(calculatedTeamData);
//                    }
//
//                    TeamInMatchData teamInMatchData = realm.createObject(TeamInMatchData.class);
//                    teamInMatchData.setMatch(match);
//                    teamInMatchData.setTeam(team);
//
//                    UploadedTeamInMatchData uploadedTeamInMatchData = realm.createObject(UploadedTeamInMatchData.class);
//                    uploadedTeamInMatchData.setMaxFieldToteHeight(-1);
//                    teamInMatchData.setUploadedData(uploadedTeamInMatchData);
//
//                    CalculatedTeamInMatchData calculatedTeamInMatchData = realm.createObject(CalculatedTeamInMatchData.class);
//                    teamInMatchData.setCalculatedData(calculatedTeamInMatchData);
//
//                    if (team.getMatchData() == null) {
//                        RealmList<TeamInMatchData> teamInMatchDatas = new RealmList<TeamInMatchData>();
//                        team.setMatchData(teamInMatchDatas);
//                    }
//
//                    team.getMatchData().add(teamInMatchData);
//
//                    match.getBlueTeams().add(team);
//                    match.getTeamInMatchDatas().add(teamInMatchData);
//                }
//            }
//
            realm.commitTransaction();
//        } catch (JSONException jsone) {
//            Log.e("test", jsone.getMessage());
//            Utils.handleError(jsone);
//        }
    }

    public void uploadDbFile(String fileName) {
        try {
            DbxFile realmFile = dbxFs.create(new DbxPath("/Database File/" + fileName));
            Log.e("test", realmFile.toString());
            Log.e("Mark", "Ready to upload databse");
            try {
                realmFile.writeFromExistingFile(new File(getFilesDir(), fileName), false);
            } catch (IOException dbxe) {
                Log.e("error", dbxe.getMessage());
            } finally {
                realmFile.close();
            }
        } catch (DbxException e) {
            Utils.handleError(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            setupDbxFs();
        }
    }
}
