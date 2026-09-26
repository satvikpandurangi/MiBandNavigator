# Changelog

## 1.1.0

- Send navigation alerts only when a supported Mi Band, Mi Smart Band, Xiaomi Smart Band, or Amazfit Band is actively connected over Bluetooth LE.
- Clear stale navigation notifications when the band disconnects or Google Maps navigation ends.
- Forward every changed Google Maps navigation payload instead of dropping same-direction distance and ETA updates.
- Keep distance and ETA refreshes silent; vibrate only for a new maneuver, including consecutive turns in the same direction onto different roads.
- Recognize additional Google Maps wording such as keep/bear left or right, continue, head, merge, and expanded notification lines.
- Add Android 12+ Nearby devices and Android 13+ notification permission handling.
- Preserve the existing Compose UI and dot-matrix notification layouts.
