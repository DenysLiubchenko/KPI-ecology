package ua.kpi.kpiecologyback.service;

import org.springframework.stereotype.Service;

@Service
public class CalcService {
    private double calcAddLadd (double ca) {
        final double defaultBW = 60, daysInYear = 365,
                defaultTout = 1.64, defaultTin = 19.69, defaultAT = 70,
                defaultVout = 0.63, defaultVin = 0.5, defaultED = 70;
        return (((ca*defaultTout*defaultVout)+(ca*defaultTin*defaultVin))*daysInYear*defaultED)/(defaultBW*defaultAT*daysInYear);
    }

    public double calcHq (double ca, double rfc) {
        return calcAddLadd(ca)*rfc;
    }

    public double calcCr (double ca, double sf) {
        return calcAddLadd(ca)*sf;
    }

    public double calcAirPenalty (double mfr, double pv, double tlv) {
        final double defaultMinWage = 6700, defaultKt = 1.55*1.25, defaultKzi = 1, t = 30744;
        if (pv - mfr <= 0) return 0;
        else if (tlv > 1)
            return getPollutionMass(mfr*0.00028, pv*0.00028, t) * defaultMinWage * (10/tlv) * defaultKt * defaultKzi;
        else
            return getPollutionMass(mfr*0.00028, pv*0.00028, t) * defaultMinWage * (1/tlv) * defaultKt * defaultKzi;
    }

    private double getPollutionMass(double mfr, double pv, double t) {
        return 3.6 * Math.pow(10, -3) * (pv - mfr) * t;
    }
}
