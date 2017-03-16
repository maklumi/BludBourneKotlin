package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

import java.util.UUID

open class Entity {

    var velocity: Vector2 = Vector2(100f, 100f)
    var boundingBox: Rectangle = Rectangle()
    var rotationDegrees = 0f
    var nextPosition: Vector2 = Vector2()
    var entityID: String = UUID.randomUUID().toString()
    var state = State.IDLE
    var currentPosition: Vector2 = Vector2()
    var currentDirection = Direction.LEFT
        private set
    var previousDirection = Direction.UP
        private set

    var WIDTH = 16f
    var HEIGHT = 16f
    private var walkingAnimRows = 0
    private var walkingAnimCols = 0
    private var walkingAnimFrames = 0

    private var walkingAnimStartRowIndex = 0

    private var walkingAnimStartColIndex = 0

    lateinit var walkingCycle: Texture

    private val idleKeyFrame = 0
    private val updatedSelectionState = false
    private val selectionSprite: Sprite? = null
    var frameTime = 0f
    lateinit var walkAnimation: Animation<TextureRegion>
    var imagePath: String = ""
    private var loadedWalkAnimationImagePath: String = ""

    var frameSprite: Sprite = Sprite()
        private set


    enum class State {
        IDLE, WALKING, ANIMATED, ANIMATE_ONCE, ANIMATE_ONCE_REVERSE, PAUSE
    }

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;

        //Gdx.app.debug(TAG, "Current Direction: " + Direction.values()[(ordinal()) % Direction.values().length] );
        //Gdx.app.debug(TAG, "Current Direction: " + ordinal() );
        //Gdx.app.debug(TAG, "Next Direction: " + Direction.values()[(ordinal()+1) % Direction.values().length] );
        val next: Direction
            get() = Direction.values()[(ordinal + 1) % Direction.values().size]

        val randomNext: Direction
            get() = Direction.values()[MathUtils.random(Direction.values().size - 1)]

        val opposite: Direction
            get() {
                if (this == LEFT) {
                    return RIGHT
                } else if (this == RIGHT) {
                    return LEFT
                } else if (this == UP) {
                    return DOWN
                } else {
                    return UP
                }
            }

    }


    constructor(entityType: String) {
        /*
		entityScript = ScriptManager.getInstance().scriptFactory(entityType);
		if( entityScript != null ){
			entityScript.create(this);
		}
		*/
    }


    fun reset() {
        frameTime = 0f
        state = State.IDLE
        rotationDegrees = 0f
        entityID = UUID.randomUUID().toString()
        WIDTH = 64f
        HEIGHT = 64f
    }

    fun update(delta: Float) {
        frameTime += delta

        loadTextures()

        val currentFrame = getCurrentFrame(delta)

        //Gdx.app.debug(TAG, "Current Region Width: " + currentFrame.getRegionWidth() + " and height: " + currentFrame.getRegionHeight()  );

        frameSprite.setRegion(currentFrame)

        //Gdx.app.debug(TAG, "FrameSprite Region Width: " + frameSprite.getRegionWidth() + " and height: " + frameSprite.getRegionHeight()  );

    }

    fun getCurrentFrame(delta: Float): TextureRegion {

        var currentFrame: TextureRegion = TextureRegion()

        if (state == State.WALKING) {
            currentFrame = walkAnimation.getKeyFrame(frameTime, false)
        } else if (state == State.ANIMATED) {
            if ((walkAnimation.playMode == Animation.PlayMode.NORMAL || walkAnimation.playMode == Animation.PlayMode.REVERSED) && walkAnimation.isAnimationFinished(frameTime)) {
                //If we are playing once (normal or reversed) and we are done, set to idle
                state = State.IDLE
                walkAnimation.playMode = Animation.PlayMode.NORMAL
                currentFrame = walkAnimation.getKeyFrame(idleKeyFrame.toFloat(), false)
            } else {
                currentFrame = walkAnimation.getKeyFrame(frameTime, false)
            }
        } else if (state == State.ANIMATE_ONCE) {
            walkAnimation.playMode = Animation.PlayMode.NORMAL
            frameTime = 0f
            state = State.ANIMATED
            currentFrame = walkAnimation.getKeyFrame(frameTime, false)
        } else if (state == State.ANIMATE_ONCE_REVERSE) {
            walkAnimation.playMode = Animation.PlayMode.REVERSED
            frameTime = 0f
            state = State.ANIMATED
            currentFrame = walkAnimation.getKeyFrame(frameTime, false)
        } else if (state == State.IDLE) {
            currentFrame = walkAnimation.getKeyFrame(idleKeyFrame.toFloat(), false)
        }

        return currentFrame
    }

    val currentFrameIndex: Int
        get() {
            var keyFrameIndex = -1
            if (state == State.WALKING || state == State.ANIMATED) {
                keyFrameIndex = walkAnimation.getKeyFrameIndex(frameTime)
            } else if (state == State.IDLE) {
                keyFrameIndex = 0
            }
            return keyFrameIndex
        }

    fun init(startX: Float, startY: Float) {
        this.currentPosition.x = startX
        this.currentPosition.y = startY
        this.nextPosition.x = startX
        this.nextPosition.y = startY

        //Gdx.app.debug(TAG, "Calling INIT" );
    }

    fun setBoundingBoxSize(percentageReduced: Float) {
        //Update the current bounding box
        val width: Float
        val height: Float
        val xOffset: Float
        val yOffset: Float

        val reductionAmount = 1.0f - percentageReduced //.8f for 20% (1 - .20)

        if (reductionAmount > 0 && reductionAmount < 1) {
            width = WIDTH * reductionAmount //reduce by 20%
            height = HEIGHT * reductionAmount //reduce by 20%
        } else {
            width = WIDTH
            height = HEIGHT
        }

        if (width == 0f || height == 0f) {
            Gdx.app.debug(TAG, "Width and Height are 0 $width:$height")
        }

        xOffset = (WIDTH - width) / 2
        yOffset = (HEIGHT - height) / 2

        //Gdx.app.debug(TAG, "Reduction amount: " + width + ":" + height);
        //Gdx.app.debug(TAG, "Regular amount: " + WIDTH + ":" + HEIGHT);
        //Gdx.app.debug(TAG, "Offset amount: " + xOffset + "," + yOffset);

        val minX = nextPosition.x + xOffset
        val minY = nextPosition.y + yOffset

        boundingBox.set(minX, minY, width, height)
        //Gdx.app.debug(TAG, "SETTING Bounding Box: " + minX + "," + minY + "width " + width + " height " + height);
    }

    private fun loadTextures() {
        //Walking animation
        if (Utility.isAssetLoaded(imagePath)) {
            walkingCycle = Utility.getTextureAsset(imagePath)
            loadedWalkAnimationImagePath = imagePath

            val walkCycleFrames = getFramesFromImage(walkingCycle)

            walkAnimation = Animation(0.11f, walkCycleFrames)
            walkAnimation.playMode = Animation.PlayMode.LOOP

            //get the first frame so we can render something
            val currentFrame = walkAnimation.getKeyFrame(idleKeyFrame.toFloat(), false)

            if (currentFrame == null) {
                Gdx.app.debug(TAG, "Current frame is null")
                return
            }

            frameSprite.setRegion(currentFrame)
            frameSprite.setOrigin((currentFrame.regionWidth / 2).toFloat(), (currentFrame.regionHeight / 2).toFloat())
            frameSprite.setSize(currentFrame.regionWidth.toFloat(), currentFrame.regionHeight.toFloat())

            //We are doing a 1 pixel for every unit for the game
            WIDTH = 1f * currentFrame.regionWidth
            HEIGHT = 1f * currentFrame.regionHeight

            //Now that the Height and Width are set, we need to set the boundingbox
            setBoundingBoxSize(0f)
        }
    }

    fun loadWalkingAnimation(numRows: Int, numColumns: Int, totalFrames: Int) {
        walkingAnimStartRowIndex = 0
        walkingAnimStartColIndex = 0

        initWalkingAnim(numRows, numColumns, totalFrames)
    }

    fun loadWalkingAnimation(startRowIndex: Int, startColIndex: Int, numRows: Int, numColumns: Int, totalFrames: Int) {
        walkingAnimStartRowIndex = startRowIndex
        walkingAnimStartColIndex = startColIndex

        initWalkingAnim(numRows, numColumns, totalFrames)
    }

    private fun initWalkingAnim(numRows: Int, numColumns: Int, totalFrames: Int) {
        walkingAnimRows = numRows
        walkingAnimCols = numColumns
        walkingAnimFrames = totalFrames

        Utility.unloadAsset(loadedWalkAnimationImagePath)
        Utility.loadTextureAsset(imagePath)
    }

    fun dispose() {
        Utility.unloadAsset(imagePath)
    }

    protected fun getFramesFromImage(sourceImage: Texture): Array<TextureRegion> {
        //Handle walking animation of main character
        val sourceCycleRow = walkingAnimRows
        val sourceCycleCol = walkingAnimCols

        val frameWidth = sourceImage.width / sourceCycleCol
        val frameHeight = sourceImage.height / sourceCycleRow

        val temp = TextureRegion.split(sourceImage, frameWidth, frameHeight)

        val textureFrames: Array<TextureRegion> = Array(walkingAnimFrames)

        var index = 0
        var i = walkingAnimStartRowIndex
        while (i < sourceCycleRow && index < walkingAnimFrames) {
            var j = walkingAnimStartColIndex
            while (j < sourceCycleCol && index < walkingAnimFrames) {
                //Gdx.app.debug(TAG, "Got frame " + i + "," + j + " from " + sourceImage);
                val region = temp[i][j]
                if (region == null) {
                    Gdx.app.debug(TAG, "Got null animation frame $i,$j from $sourceImage")
                }
                textureFrames[index] = region
                index++
                j++
            }
            i++
        }

        return textureFrames
    }

    fun setDirection(direction: Direction) {
        this.previousDirection = this.currentDirection
        this.currentDirection = direction

        //Look into the appropriate variable when changing position

        when (currentDirection) {
            Entity.Direction.DOWN -> rotationDegrees = 0f
            Entity.Direction.LEFT -> rotationDegrees = 270f
            Entity.Direction.UP -> rotationDegrees = 180f
            Entity.Direction.RIGHT -> rotationDegrees = 90f
            else -> {
            }
        }

        frameSprite.rotation = rotationDegrees
    }


    fun setNextPositionToCurrent() {
        if (state == State.PAUSE) return

        frameSprite.x = nextPosition.x
        frameSprite.y = nextPosition.y
        currentPosition.set(nextPosition.x, nextPosition.y)
        //Gdx.app.debug(TAG, "NOT BLOCKED: Setting nextPosition as Current: (" + nextPlayerPosition.x + "," + nextPlayerPosition.y + ")"  );
    }


    fun calculateNextPosition(currentDirection: Direction, deltaTime: Float) {
        if (state == State.PAUSE) return

        var testX = currentPosition.x
        var testY = currentPosition.y


        //Gdx.app.debug(TAG, "calculateNextPosition:: Current Position: (" + currentPlayerPosition.x + "," + currentPlayerPosition.y + ")"  );
        //Gdx.app.debug(TAG, "calculateNextPosition:: Current Direction: " + currentDirection  );

        velocity.scl(deltaTime)

        when (currentDirection) {
            Entity.Direction.LEFT -> testX -= velocity.x
            Entity.Direction.RIGHT -> testX += velocity.x
            Entity.Direction.UP -> testY += velocity.y
            Entity.Direction.DOWN -> testY -= velocity.y
        }

        nextPosition.x = testX
        nextPosition.y = testY

        //velocity
        velocity.scl(1 / deltaTime)
    }


    private val TAG = Entity::class.java.simpleName


}
