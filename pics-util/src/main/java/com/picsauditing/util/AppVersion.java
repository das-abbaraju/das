package com.picsauditing.util;

import org.apache.commons.lang3.math.NumberUtils;

public class AppVersion {
    // TODO Move current to pics-web somehow
    // Update Current Version each release
    static public AppVersion current = new AppVersion(7, 2, 6);

    private int major;
    private int minor;
    private int patch;

    public AppVersion() {
    }

    public AppVersion(int major, int minor) {
        this.major = major;
        this.minor = minor;
    }

    public AppVersion(String major, String minor) {
        this.major = NumberUtils.toInt(major, 0);
        this.minor = NumberUtils.toInt(minor, 0);
    }

    public AppVersion(String major, String minor, String patch) {
        this.major = NumberUtils.toInt(major, 0);
        this.minor = NumberUtils.toInt(minor, 0);
        this.patch = NumberUtils.toInt(patch, 0);
    }

    public AppVersion(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public AppVersion(String versionString) {
        String[] versionPieces = versionString.split("\\.");

        this.major = NumberUtils.toInt(versionPieces[0], 0);
        if (versionPieces.length > 1)
            this.minor = NumberUtils.toInt(versionPieces[1], 0);
    }

    public String getVersion() {
        if (patch > 0)
            return major + "." + minor + "." + patch;
        return major + "." + minor;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public boolean greaterThan(AppVersion other) {
        if (this.major != other.major) {
            return this.major > other.major;
        } else if (this.minor != other.minor) {
            return this.minor > other.minor;
        } else {
            return this.patch > other.patch;
        }
    }

    public boolean greaterThan(int maj, int min) {
        return greaterThan(new AppVersion(maj, min));
    }

    public boolean greaterThan(int maj, int min, int patch) {
        return greaterThan(new AppVersion(maj, min, patch));
    }

    public boolean greaterThanOrEqualTo(int maj, int min) {
        AppVersion other = new AppVersion(maj, min);
        if (this.equals(other))
            return true;
        return this.greaterThan(other);
    }

    public boolean equals(Object obj) {
        AppVersion other = (AppVersion) obj;
        return this.major == other.major && this.minor == other.minor && this.patch == other.patch;
    }
}
