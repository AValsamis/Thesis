package uoa.di.gr.thesis.interfaces;

import java.util.List;

import uoa.di.gr.thesis.entities.Wifi;

public interface RegisterZoneCallbacks {
        public void onGatherWifiList(List<Wifi> wifis, Boolean isSafe);
        public void onRegisterZoneSuccess(String response);
        public void onRegisterZoneFailure(String response);
    }

