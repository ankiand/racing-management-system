package se.cag.labs.currentrace.timer;


import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se.cag.labs.currentrace.services.repository.CurrentRaceRepository;
import se.cag.labs.currentrace.services.repository.datamodel.RaceStatus;

import java.util.TimerTask;

@Component
@Log
public class VerifyRacePassagesTimerTask extends TimerTask {

    public static final long TIME_INTERVAL = 10 * 1000;
    public static final long TIME_LIMIT = 3 * 60 * 1000;

    @Autowired
    private CurrentRaceRepository repository;

    @Override
    public void run() {
        RaceStatus raceStatus = repository.findByRaceId(RaceStatus.ID);
        if (isActiveAndConsistent(raceStatus)) {
            long currentTime = System.currentTimeMillis();
            if (raceStatus.getStartTime() == null) {
                if (currentTime - raceStatus.getRaceActivatedTime() >= TIME_LIMIT) {
                    raceStatus.setEvent(RaceStatus.Event.TIME_OUT_NOT_STARTED);
                    raceStatus.setState(RaceStatus.State.INACTIVE);
                }
            }
            if (raceStatus.getMiddleTime() == null && raceStatus.getStartTime() != null) {
                if (currentTime - raceStatus.getStartTime() >= TIME_LIMIT) {
                    raceStatus.setEvent(RaceStatus.Event.DISQUALIFIED);
                    raceStatus.setState(RaceStatus.State.INACTIVE);
                }
            }
            if (raceStatus.getFinishTime() == null && raceStatus.getMiddleTime() != null) {
                if (currentTime - raceStatus.getMiddleTime() >= TIME_LIMIT) {
                    raceStatus.setEvent(RaceStatus.Event.TIME_OUT_NOT_FINISHED);
                    raceStatus.setState(RaceStatus.State.INACTIVE);
                }
            }
            log.info(raceStatus.toString());
            repository.save(raceStatus);
        }
    }

    private boolean isActiveAndConsistent(RaceStatus raceStatus) {
        return raceStatus != null && raceStatus.getRaceActivatedTime() != null && raceStatus.getState() == RaceStatus.State.ACTIVE;
    }


}