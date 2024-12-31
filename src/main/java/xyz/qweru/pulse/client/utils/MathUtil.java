package xyz.qweru.pulse.client.utils;

public class MathUtil {
    public static float smoothDamp(float current, float target, float[] velocity, float smoothTime, float maxSpeed, float deltaTime) {
        smoothTime = Math.max(0.0001f, smoothTime); // Avoid division by zero

        // Calculate the damping factor
        float omega = 2.0f / smoothTime;

        // Calculate the exponential decay term
        float x = omega * deltaTime;
        float exp = 1.0f / (1.0f + x + 0.48f * x * x + 0.235f * x * x * x);

        // Calculate the change and clamp it to the max speed
        float change = current - target;
        float maxChange = maxSpeed * smoothTime;
        change = Math.min(Math.max(change, -maxChange), maxChange);

        // Update target to account for the clamped change
        float tempTarget = target + change;

        // Update velocity and smooth the current position
        float tempVel = (velocity[0] + omega * change) * deltaTime;
        velocity[0] = (velocity[0] - omega * tempVel) * exp;
        float newPosition = tempTarget + (change + tempVel) * exp;

        // If we've reached the target or overshot, return the target to stop oscillation
        if ((target - current > 0.0f && newPosition > target) || (target - current < 0.0f && newPosition < target)) {
            newPosition = target;
            velocity[0] = 0.0f;
        }

        return newPosition;
    }
}
