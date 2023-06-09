// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

//import com.ctre.phoenix.motorcontrol.FeedbackDevice;
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

public class ShoulderSub extends SubsystemBase {
    /**
     * Creates a new ElevatorTest.
     */

    private final TalonFX shoulderMotorOne;

    // final int kUnitsPerRevolution = 2048; /* this is constant for Talon FX */

    private static final double k_openLoopRampRate = 0.1;
    private static final double k_currentLimit = Constants.Shoulder.currentLimit; // Current limit for intake falcon 500

    // private final TrapezoidProfile.Constraints m_constraints = new
    // TrapezoidProfile.Constraints(1.75, 0.75);
    // private final ProfiledPIDController m_controller = new
    // ProfiledPIDController(1.3, 0.0, 0.7, m_constraints, kDt);
    private double m_encoder = 0;
    private double m_goalPosition;

    private static ShoulderSub INSTANCE;

    public static ShoulderSub getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ShoulderSub();
        }
        return INSTANCE;
    }

    private ShoulderSub() {

        // initialize motors
        // the right motor will spin clockwise and the left motor will go counter
        // clockwise
        shoulderMotorOne = new TalonFX(Constants.Shoulder.ShoulderMotorID);

        TalonFXConfiguration config = new TalonFXConfiguration();
        config.voltageCompSaturation = 12.0;
        config.openloopRamp = k_openLoopRampRate;
        config.statorCurrLimit = new StatorCurrentLimitConfiguration(true, k_currentLimit, 0, 0);

        shoulderMotorOne.configAllSettings(config);
        shoulderMotorOne.enableVoltageCompensation(true);
        shoulderMotorOne.setNeutralMode(NeutralMode.Brake);
        shoulderMotorOne.setInverted(TalonFXInvertType.Clockwise);
        //ShoulderMotorOne.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor);
        shoulderMotorOne.setStatusFramePeriod(StatusFrameEnhanced.Status_2_Feedback0, 20);

        /* Config the sensor used for Primary PID and sensor direction */
        shoulderMotorOne.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor,
                Constants.Shoulder.kPIDLoopIdx,
                Constants.Shoulder.kTimeoutMs);

        shoulderMotorOne.setSensorPhase(Constants.Shoulder.kSensorPhase);

        shoulderMotorOne.configAllowableClosedloopError(0, Constants.Shoulder.kShoulderDeadband,
                Constants.Shoulder.kTimeoutMs);
        /* Config Position Closed Loop gains in slot0, tsypically kF stays zero. */
        // ShoulderMotorOne.config_kF(Constants.Elevator.kPIDLoopIdx,
        // Constants.Elevator. kGains.kF, Constants.Elevator.kTimeoutMs);
        shoulderMotorOne.config_kP(Constants.Shoulder.kPIDLoopIdx, Constants.Shoulder.shoulderKP,
                Constants.Shoulder.kTimeoutMs);
        shoulderMotorOne.config_kI(Constants.Shoulder.kPIDLoopIdx, Constants.Shoulder.shoulderKI,
                Constants.Shoulder.kTimeoutMs);
        shoulderMotorOne.config_kD(Constants.Shoulder.kPIDLoopIdx, Constants.Shoulder.shoulderKD,
                Constants.Shoulder.kTimeoutMs);
        // ShoulderMotorOne.config_kF(Constants.Shoulder.kPIDLoopIdx, Constants.Shoulder.ShoulderKF,
        //     Constants.Shoulder.kTimeoutMs);

        shoulderMotorOne.setSelectedSensorPosition(0);
        m_encoder = shoulderMotorOne.getSelectedSensorPosition();
        // m_encoder = ShoulderMotorOne.getSelectedSensorPosition(); // * 1.0 / 360.0 *
        // 2.0 * Math.PI * 1.5;

    }

    public void setPosition(double goalPosition) {
        // if (goalPosition > Constants.Shoulder.lowerLimit) {
        //   goalPosition = Constants.Shoulder.lowerLimit;
        // } else if (goalPosition < Constants.Shoulder.upperLimit) {
        //   goalPosition = Constants.Shoulder.upperLimit;
        // }

        m_goalPosition = goalPosition;

    }

    public void joystickPosition(double joystickPosition) {
        m_goalPosition = m_goalPosition - joystickPosition;
    }

    public double getShoulderPosition() {
        return m_encoder;
    }

    @Override
    public void periodic() {
        // This method will be called once per scheduler run
        if (RobotBase.isReal()) {
            m_encoder = shoulderMotorOne.getSelectedSensorPosition();
        } else {
            m_encoder = m_goalPosition; // motor immediately gets to its desired position if not real
        }

        if (m_goalPosition > Constants.Shoulder.maxExtension) {
            m_goalPosition = Constants.Shoulder.maxExtension;
        } else if (m_goalPosition < Constants.Shoulder.minExtension) {
            m_goalPosition = Constants.Shoulder.minExtension;
        }
        SmartDashboard.putNumber("Shoulder Position", m_encoder);
        SmartDashboard.putNumber("Shoulder Goal Position", m_goalPosition);
        SmartDashboard.putNumber("Shoulder motor Output", shoulderMotorOne.getMotorOutputPercent());
        // ShoulderMotorOne.set(ControlMode.Position,
        // m_controller.calculate(m_encoder));
        shoulderMotorOne.set(TalonFXControlMode.Position, m_goalPosition);

    }

    public boolean atSetpoint() {
        return getShoulderPosition() <= m_goalPosition + Constants.Shoulder.kShoulderAllowableRange
                && getShoulderPosition() >= m_goalPosition - Constants.Shoulder.kShoulderAllowableRange;

    }
}
