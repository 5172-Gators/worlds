// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
//import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;


import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import frc.robot.Constants;

public class ElevatorSub extends SubsystemBase {
    /**
     * Creates a new ElevatorTest.
     */

    private final TalonFX elevatorMotorOne;
    private final TalonFX elevatorMotorTwo;
    // final int kUnitsPerRevolution = 2048; /* this is constant for Talon FX */

    private static final double k_openLoopRampRate = 0.1;
    private static final int k_currentLimit = Constants.Elevator.currentLimit; // Current limit for intake falcon 500

    private double m_encoder = 0;
    private double m_goalPosition;

    private static ElevatorSub INSTANCE;

    public static ElevatorSub getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ElevatorSub();
        }
        return INSTANCE;
    }


    private ElevatorSub() {

        // initialize motors
        // the right motor will spin clockwise and the left motor will go counter
        // clockwise
        elevatorMotorOne = new TalonFX(Constants.Elevator.motorOneId);
        elevatorMotorTwo = new TalonFX(Constants.Elevator.motorTwoId);

        TalonFXConfiguration config = new TalonFXConfiguration();
        config.voltageCompSaturation = 12.0;
        config.openloopRamp = k_openLoopRampRate;
        config.statorCurrLimit = new StatorCurrentLimitConfiguration(true, k_currentLimit, 0, 0);

        elevatorMotorOne.configAllSettings(config);
        elevatorMotorOne.enableVoltageCompensation(true);
        elevatorMotorOne.setNeutralMode(NeutralMode.Brake);
        elevatorMotorOne.setInverted(TalonFXInvertType.CounterClockwise);
        elevatorMotorOne.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        elevatorMotorOne.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 20);
        // elevatorMotorOne.setSelectedSensorPosition(0); // zero the encoder

        elevatorMotorTwo.configAllSettings(config);
        elevatorMotorTwo.enableVoltageCompensation(true);
        elevatorMotorTwo.setNeutralMode(NeutralMode.Brake);
        elevatorMotorTwo.setInverted(TalonFXInvertType.CounterClockwise);
        elevatorMotorTwo.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);

        elevatorMotorTwo.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 20);

        /* Config the sensor used for Primary PID and sensor direction */
        elevatorMotorOne.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor,
                Constants.Elevator.kPIDLoopIdx,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorTwo.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor,
                Constants.Elevator.kPIDLoopIdx,
                Constants.Elevator.kTimeoutMs);

        elevatorMotorOne.setSensorPhase(Constants.Elevator.kSensorPhase);
        elevatorMotorTwo.setSensorPhase(Constants.Elevator.kSensorPhase);

        elevatorMotorOne.configAllowableClosedloopError(Constants.Elevator.kRisingSlotIdx, Constants.Elevator.kElevatorDeadband,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorTwo.configAllowableClosedloopError(Constants.Elevator.kRisingSlotIdx, Constants.Elevator.kElevatorDeadband,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorOne.configAllowableClosedloopError(Constants.Elevator.kFallingSlotIdx, Constants.Elevator.kElevatorDeadband,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorTwo.configAllowableClosedloopError(Constants.Elevator.kFallingSlotIdx, Constants.Elevator.kElevatorDeadband,
                Constants.Elevator.kTimeoutMs);
        /* Config Position Closed Loop gains in slot0, tsypically kF stays zero. */
        // elevatorMotorOne.config_kF(Constants.Elevator.kPIDLoopIdx,
        // Constants.Elevator. kGains.kF, Constants.Elevator.kTimeoutMs);
        elevatorMotorOne.config_kP(Constants.Elevator.kRisingSlotIdx, Constants.Elevator.elevatorKP,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorOne.config_kI(Constants.Elevator.kRisingSlotIdx, Constants.Elevator.elevatorKI,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorOne.config_kD(Constants.Elevator.kRisingSlotIdx, Constants.Elevator.elevatorKD,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorOne.config_kF(Constants.Elevator.kRisingSlotIdx, Constants.Elevator.elevatorKF,
                Constants.Elevator.kTimeoutMs);

        elevatorMotorTwo.config_kP(Constants.Elevator.kRisingSlotIdx, Constants.Elevator.elevatorKP,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorTwo.config_kI(Constants.Elevator.kRisingSlotIdx, Constants.Elevator.elevatorKI,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorTwo.config_kD(Constants.Elevator.kRisingSlotIdx, Constants.Elevator.elevatorKD,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorTwo.config_kF(Constants.Elevator.kRisingSlotIdx, Constants.Elevator.elevatorKF,
                Constants.Elevator.kTimeoutMs);

        // config PID for elevator to use when elevator is lowering - lower P so it falls less fast
        elevatorMotorOne.config_kP(Constants.Elevator.kFallingSlotIdx, Constants.Elevator.fallingElevatorKP,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorOne.config_kI(Constants.Elevator.kFallingSlotIdx, Constants.Elevator.elevatorKI,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorOne.config_kD(Constants.Elevator.kFallingSlotIdx, Constants.Elevator.elevatorKD,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorOne.config_kF(Constants.Elevator.kFallingSlotIdx, Constants.Elevator.elevatorKF,
                Constants.Elevator.kTimeoutMs);

        elevatorMotorTwo.config_kP(Constants.Elevator.kFallingSlotIdx, Constants.Elevator.fallingElevatorKP,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorTwo.config_kI(Constants.Elevator.kFallingSlotIdx, Constants.Elevator.elevatorKI,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorTwo.config_kD(Constants.Elevator.kFallingSlotIdx, Constants.Elevator.elevatorKD,
                Constants.Elevator.kTimeoutMs);
        elevatorMotorTwo.config_kF(Constants.Elevator.kFallingSlotIdx, Constants.Elevator.elevatorKF,
                Constants.Elevator.kTimeoutMs);

        //elevatorMotorOne.setSelectedSensorPosition(m_encoder);
        //elevatorMotorTwo.setSelectedSensorPosition();
        elevatorMotorTwo.follow(elevatorMotorOne);

        elevatorMotorOne.setSelectedSensorPosition(0);
        elevatorMotorTwo.setSelectedSensorPosition(0);

        m_encoder = elevatorMotorOne.getSelectedSensorPosition(); // * 1.0 / 360.0 * 2.0 * Math.PI * 1.5;

    }

    public void setPosition(double goalPosition) {
        m_goalPosition = goalPosition;
    }

    public void joystickPosition(double joystickPosition) {
        m_goalPosition = m_goalPosition + joystickPosition;
    }

    public double getElevatorPosition() {
        return m_encoder;
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        if (RobotBase.isReal()) {
            m_encoder = elevatorMotorOne.getSelectedSensorPosition();
        } else {
            m_encoder = m_goalPosition; // motor immediately gets to its desired position if not real
        }

        if (m_goalPosition > Constants.Elevator.maxExtension) {
            m_goalPosition = Constants.Elevator.maxExtension;
        } else if (m_goalPosition < Constants.Elevator.minExtension) {
            m_goalPosition = Constants.Elevator.minExtension;
        }
        SmartDashboard.putNumber("Elevator Position", m_encoder);
        SmartDashboard.putNumber("Elevator Goal Position", m_goalPosition);

        //if goal position is lower than act position, use falling PID slot
        if (m_goalPosition + Constants.Elevator.kElevatorAllowableRange < m_encoder) {
            elevatorMotorOne.selectProfileSlot(Constants.Elevator.kFallingSlotIdx, Constants.Elevator.kPIDLoopIdx);
        } else {
            elevatorMotorOne.selectProfileSlot(Constants.Elevator.kRisingSlotIdx, Constants.Elevator.kPIDLoopIdx);
        }

        elevatorMotorOne.set(TalonFXControlMode.Position, m_goalPosition);
        elevatorMotorTwo.follow(elevatorMotorOne);

        //elevatorMotorTwo.set(TalonFXControlMode.Position, m_goalPosition);
        SmartDashboard.putNumber("Elevator MotorOne Percentage", elevatorMotorOne.getMotorOutputPercent());
        SmartDashboard.putNumber("Elevator MotorTwo Percentage", elevatorMotorTwo.getMotorOutputPercent());

    }

    public boolean atSetpoint() {
        return getElevatorPosition() >= m_goalPosition - Constants.Elevator.kElevatorAllowableRange
                && getElevatorPosition() <= m_goalPosition + Constants.Elevator.kElevatorAllowableRange;

    }
}
