package ua.kpi.kpiecologyback.service;

import org.springframework.stereotype.Service;

@Service
public class CalcService {
    public double calcAddLadd (double ca) {
        final double defaultBW = 60, daysInYear = 365,
                defaultTout = 1.64, defaultTin = 19.69, defaultAT = 70,
                defaultVout = 0.63, defaultVin = 0.5, defaultED = 70;
        return (((ca*defaultTout*defaultVout)+(ca*defaultTin*defaultVin))*daysInYear*defaultED)/(defaultBW*defaultAT*daysInYear);
    }
}
