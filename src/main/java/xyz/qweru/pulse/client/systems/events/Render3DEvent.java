package xyz.qweru.pulse.client.systems.events;

import meteordevelopment.orbit.ICancellable;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

public class Render3DEvent implements ICancellable {
    boolean c = false;
    @Override
    public void setCancelled(boolean cancelled) {
        c = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return c;
    }

    RenderTickCounter tickCounter;
    boolean renderBlockOutline;
    Camera camera;
    MatrixStack matrixStack;
    private final Frustum frustum;

    public Frustum getFrustum() {
        return frustum;
    }

    public Render3DEvent(Matrix4f positionMatrix, Matrix4f projectionMatrix, LightmapTextureManager lightmapTextureManager, GameRenderer gameRenderer, Camera camera, boolean renderBlockOutline, RenderTickCounter tickCounter, MatrixStack stack, Frustum frustum) {
        this.positionMatrix = positionMatrix;
        this.projectionMatrix = projectionMatrix;
        this.lightmapTextureManager = lightmapTextureManager;
        this.gameRenderer = gameRenderer;
        this.camera = camera;
        this.renderBlockOutline = renderBlockOutline;
        this.tickCounter = tickCounter;
        this.matrixStack = stack;
        this.frustum = frustum;
    }

    GameRenderer gameRenderer;

    public RenderTickCounter getTickCounter() {
        return tickCounter;
    }

    public boolean isRenderBlockOutline() {
        return renderBlockOutline;
    }

    public Camera getCamera() {
        return camera;
    }

    public GameRenderer getGameRenderer() {
        return gameRenderer;
    }

    public LightmapTextureManager getLightmapTextureManager() {
        return lightmapTextureManager;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Matrix4f getPositionMatrix() {
        return positionMatrix;
    }

    public MatrixStack getMatrixStack() {
        return matrixStack;
    }

    LightmapTextureManager lightmapTextureManager;
    Matrix4f projectionMatrix;
    Matrix4f positionMatrix;

}
