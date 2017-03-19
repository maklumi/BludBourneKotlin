package com.packtpub.libgdx.bludbourne

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Array

class Entity {

    var velocity: Vector2 = Vector2(2f, 2f)
    var state = State.IDLE
    var currentPlayerPosition: Vector2 = Vector2()
    var nextPlayerPosition: Vector2 = Vector2()
    var currentDirection: Direction = Direction.LEFT
        private set
    var previousDirection: Direction = Direction.UP
        private set

    var boundingBox: Rectangle = Rectangle()

    private val defaultSpritePath = "sprites/characters/Warrior.png"
    var FRAME_WIDTH = 16
    var FRAME_HEIGHT = 16

    var frameTime = 0f

    var frameSprite: Sprite = Sprite()
        private set
    lateinit var currentFrame: TextureRegion
    private lateinit var walkLeftAnimation: Animation<TextureRegion>
    private lateinit var walkRightAnimation: Animation<TextureRegion>
    private lateinit var walkUpAnimation: Animation<TextureRegion>
    private lateinit var walkDownAnimation: Animation<TextureRegion>

    private lateinit var walkLeftFrames: Array<TextureRegion>
    private lateinit var walkRightFrames: Array<TextureRegion>
    private lateinit var walkUpFrames: Array<TextureRegion>
    private lateinit var walkDownFrames: Array<TextureRegion>

    enum class State {
        IDLE, WALKING, ANIMATED, ANIMATE_ONCE, ANIMATE_ONCE_REVERSE, PAUSE
    }

    enum class Direction {
        UP, RIGHT, DOWN, LEFT;

        val next: Direction
            get() = Direction.values()[(ordinal + 1) % Direction.values().size]

        val randomNext: Direction
            get() = Direction.values()[MathUtils.random(Direction.values().size - 1)]

        val opposite: Direction
            get() {
                if (this == LEFT) return RIGHT
                else if (this == RIGHT) return LEFT
                else if (this == UP) return DOWN
                else return UP
            }

    }

    init {
        Utility.loadTextureAsset(defaultSpritePath)
        loadDefaultSprite()
        loadAllAnimations()
    }

    fun update(delta: Float) {
        frameTime = (frameTime + delta) % 5
        setBoundingBoxSize(0f, 0.5f) // set bound to lower half of body for better feel

    }


    fun init(startX: Float, startY: Float) {
        this.currentPlayerPosition.x = startX
        this.currentPlayerPosition.y = startY
        this.nextPlayerPosition.x = startX
        this.nextPlayerPosition.y = startY

    }


    fun setBoundingBoxSize(percentageWidthReduced: Float, percentageHeightReduced: Float) {
        val width: Float
        val height: Float

        val widthReductionAmount = 1.0f - percentageWidthReduced //.8f for 20% (1 - .20)
        val heightReductionAmount = 1.0f - percentageHeightReduced //.8f for 20% (1 - .20)

        if (widthReductionAmount > 0 && widthReductionAmount < 1) {
            width = FRAME_WIDTH * widthReductionAmount
        } else {
            width = FRAME_WIDTH.toFloat()
        }

        if (heightReductionAmount > 0 && heightReductionAmount < 1) {
            height = FRAME_HEIGHT * heightReductionAmount
        } else {
            height = FRAME_HEIGHT.toFloat()
        }


        if (width == 0f || height == 0f) {
            Gdx.app.debug(TAG, "Width and Height are 0!! $width:$height")
        }

        val minX = nextPlayerPosition.x
        val minY = nextPlayerPosition.y

        boundingBox.set(minX, minY, width, height)
        //Gdx.app.debug(TAG, "SETTING Bounding Box: (" + minX + "," + minY + ")  width: " + width + " height: " + height);
    }


    fun setDirection(direction: Direction, delta: Float) {
        this.previousDirection = this.currentDirection
        this.currentDirection = direction

        when (currentDirection) {
            Entity.Direction.DOWN -> currentFrame = walkDownAnimation.getKeyFrame(frameTime)
            Entity.Direction.LEFT -> currentFrame = walkLeftAnimation.getKeyFrame(frameTime)
            Entity.Direction.UP -> currentFrame = walkUpAnimation.getKeyFrame(frameTime)
            Entity.Direction.RIGHT -> currentFrame = walkRightAnimation.getKeyFrame(frameTime)
        }

    }


    fun setNextPositionToCurrent() {

        frameSprite.x = nextPlayerPosition.x
        frameSprite.y = nextPlayerPosition.y
        currentPlayerPosition.set(nextPlayerPosition.x, nextPlayerPosition.y)
    }


    fun calculateNextPosition(currentDirection: Direction, deltaTime: Float) {

        var testX = currentPlayerPosition.x
        var testY = currentPlayerPosition.y

        velocity.scl(deltaTime)

        when (currentDirection) {
            Entity.Direction.LEFT -> testX -= velocity.x
            Entity.Direction.RIGHT -> testX += velocity.x
            Entity.Direction.UP -> testY += velocity.y
            Entity.Direction.DOWN -> testY -= velocity.y
        }

        nextPlayerPosition.x = testX
        nextPlayerPosition.y = testY

        //velocity
        velocity.scl(1 / deltaTime)
    }

    // private functions
    private fun loadDefaultSprite() {
        val texture = Utility.getTextureAsset(defaultSpritePath)
        val textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT)
        frameSprite = Sprite(textureFrames[0][0], 0, 0, FRAME_WIDTH, FRAME_HEIGHT)
        // default first frame
        currentFrame = textureFrames[0][0]
    }

    private fun loadAllAnimations() {
        val texture = Utility.getTextureAsset(defaultSpritePath)
        val textureFrames = TextureRegion.split(texture, FRAME_WIDTH, FRAME_HEIGHT)

        walkDownFrames = Array<TextureRegion>(4)
        walkLeftFrames = Array<TextureRegion>(4)
        walkRightFrames = Array<TextureRegion>(4)
        walkUpFrames = Array<TextureRegion>(4)

        for (i in 0..3) {
            for (j in 0..3) {
                val region = textureFrames[i][j]
                when (i) {
                    0 -> walkDownFrames.insert(j, region)
                    1 -> walkLeftFrames.insert(j, region)
                    2 -> walkRightFrames.insert(j, region)
                    3 -> walkUpFrames.insert(j, region)
                }
            }
        }

        walkDownAnimation = Animation(0.25f, walkDownFrames, Animation.PlayMode.LOOP)
        walkLeftAnimation = Animation(0.25f, walkLeftFrames, Animation.PlayMode.LOOP)
        walkRightAnimation = Animation(0.25f, walkRightFrames, Animation.PlayMode.LOOP)
        walkUpAnimation = Animation(0.25f, walkUpFrames, Animation.PlayMode.LOOP)

    }

    private val TAG = Entity::class.java.simpleName


}
