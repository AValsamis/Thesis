package uoa.di.gr.thesis.interfaces;

import java.util.List;

import uoa.di.gr.thesis.entities.Wifi;

/**
 * Created by Angelos on 11/21/2016.
 */

public interface RegisterZoneCallbacks {
        public void onGatherWifiList(List<Wifi> wifis);
        public void onRegisterZoneSuccess(String response);
        public void onRegisterZoneFailure(String response);
    }

