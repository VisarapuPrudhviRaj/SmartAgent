package nk.mobleprojects.smartagent.utils;


public interface PermissionResult {

    void permissionGranted();

    void permissionDenied();

    void permissionForeverDenied();

}
