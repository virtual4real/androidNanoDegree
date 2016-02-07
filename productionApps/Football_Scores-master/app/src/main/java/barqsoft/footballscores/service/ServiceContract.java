package barqsoft.footballscores.service;

/**
 * Created by ioanagosman on 21/01/16.
 */
public class ServiceContract {

    //JSON data
    // This set of league codes is for the 2015/2016 season. In fall of 2016, they will need to
    // be updated. Feel free to use the codes
    public static final String BUNDESLIGA1 = "394";
    public static final String BUNDESLIGA2 = "395";
    public static final String LIGUE1 = "396";
    public static final String LIGUE2 = "397";
    public static final String PREMIER_LEAGUE = "398";
    public static final String PRIMERA_DIVISION = "399";
    public static final String SEGUNDA_DIVISION = "400";
    public static final String SERIE_A = "401";
    public static final String PRIMERA_LIGA = "402";
    public static final String Bundesliga3 = "403";
    public static final String EREDIVISIE = "404";


    public static final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
    public static final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
    public static final String FIXTURES = "fixtures";
    public static final String LINKS = "_links";
    public static final String SOCCER_SEASON = "soccerseason";
    public static final String SELF = "self";
    public static final String MATCH_DATE = "date";
    public static final String HOME_TEAM = "homeTeamName";
    public static final String AWAY_TEAM = "awayTeamName";
    public static final String RESULT = "result";
    public static final String HOME_GOALS = "goalsHomeTeam";
    public static final String AWAY_GOALS = "goalsAwayTeam";
    public static final String MATCH_DAY = "matchday";


    public ServiceContract(){

    }
}
