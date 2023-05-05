package frc.robot.autos;

import edu.wpi.first.math.controller.HolonomicDriveController;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.SwerveControllerCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.Constants.GamePiece;
import frc.robot.Constants.Position;
import frc.robot.Robot;
import frc.robot.commands.Drive.DriveThenAutoBalance;
import frc.robot.commands.Drive.JustAutobalance;
import frc.robot.commands.Drive.ResetDriveOdometry;
import frc.robot.commands.Intake.IntakeOn;
import frc.robot.commands.Intake.intakeStop;
import frc.robot.commands.SetAllPositions;
import frc.robot.subsystems.*;

import java.util.List;

public class TrajectoryPlaceCubeExitCommunityAutoBalance extends SequentialCommandGroup { // places cube high, exits community, autobalances
    private final IntakeSub s_Intake = IntakeSub.getInstance();
    private final ElevatorSub s_Elevator = ElevatorSub.getInstance();//new ElevatorSub();
    private final WristSub s_Wrist = WristSub.getInstance();
    private final ShoulderSub s_Shoulder = ShoulderSub.getInstance();

    private Pose2d initialRobotPos = new Pose2d(); // note this is not the intial TRAJECTORY POSITION, but the position of the robot

    public TrajectoryPlaceCubeExitCommunityAutoBalance(Swerve s_Swerve) {

        // hard coding field position for simulation
        DriverStation.Alliance color = RobotBase.isReal() ? DriverStation.getAlliance() : DriverStation.Alliance.Blue;

        // because depending on if ur blue or red, forwards on the field is backwards relative to your respective driverstation
        Rotation2d forwardRelativeToDriver = color == DriverStation.Alliance.Blue ? Rotation2d.fromDegrees(0) : Rotation2d.fromDegrees(180);
        Rotation2d backwardsRelativeToDriver = color == DriverStation.Alliance.Blue ? Rotation2d.fromDegrees(180) : Rotation2d.fromDegrees(0);

        // trajectory generation
        TrajectoryConfig config = new TrajectoryConfig(Constants.AutoConstants.kMaxSpeedMetersPerSecond, Constants.AutoConstants.kMaxAccelerationMetersPerSecondSquared).setKinematics(Constants.Swerve.swerveKinematics);
        // An example trajectory to follow.  All units in meters.
        Trajectory startToAutobalance = TrajectoryGenerator.generateTrajectory(
                // Pass through these two interior waypoints
                List.of(new Pose2d(1.58, 2.77, forwardRelativeToDriver), new Pose2d(4.58, 2.77, forwardRelativeToDriver)),
                config
        );

        Trajectory autoBalanceToOutsideCommunity = TrajectoryGenerator.generateTrajectory(
                // Pass through these two interior waypoints
                List.of(new Pose2d(4.58, 2.77, forwardRelativeToDriver), new Pose2d(6.58, 2.77, forwardRelativeToDriver)),
                config
        );

<<<<<<< Updated upstream
        Trajectory outsideCommunityToAutoBalance = TrajectoryGenerator.generateTrajectory(
                // Pass through these two interior waypoints
                List.of(new Pose2d(6.58, 2.77, backwardsRelativeToDriver), new Pose2d(4.58, 2.77, backwardsRelativeToDriver)),
                config
        );
=======
            autoBalanceToOutsideCommunity = TrajectoryGenerator.generateTrajectory(
                    // Pass through these two interior waypoints
                    List.of(new Pose2d(4.4, 2.77, forwardRelativeToDriver), new Pose2d(6.58, 2.77, forwardRelativeToDriver)),
                    config
            );

            outsideCommunityToAutoBalance = TrajectoryGenerator.generateTrajectory(
                    // Pass through these two interior waypoints
                    List.of(new Pose2d(6., 2.77, backwardsRelativeToDriver), new Pose2d(4.58, 2.77, backwardsRelativeToDriver)),
                    config
            );
        } else {
            double fieldXLength = 16; // abt 16 meters
            startToAutobalance = TrajectoryGenerator.generateTrajectory(
                    // Pass through these two interior waypoints
                    List.of(new Pose2d(fieldXLength - 1.58, 2.77, forwardRelativeToDriver), new Pose2d(fieldXLength - 4.58, 2.77, forwardRelativeToDriver)),
                    config
            );

            autoBalanceToOutsideCommunity = TrajectoryGenerator.generateTrajectory(
                    // Pass through these two interior waypoints
                    List.of(new Pose2d(fieldXLength - 4.58, 2.77, forwardRelativeToDriver), new Pose2d(fieldXLength - 5.75, 2.77, forwardRelativeToDriver)),
                    config
            );

            outsideCommunityToAutoBalance = TrajectoryGenerator.generateTrajectory(
                    // Pass through these two interior waypoints
                    List.of(new Pose2d(fieldXLength - 5.75, 2.77, backwardsRelativeToDriver), new Pose2d(fieldXLength - 5.00, 2.77, backwardsRelativeToDriver)),
                    config
            );
        }
>>>>>>> Stashed changes

        var thetaController = new ProfiledPIDController(Constants.AutoConstants.kPThetaController, 0, 0, Constants.AutoConstants.kThetaControllerConstraints);
        thetaController.enableContinuousInput(-Math.PI, Math.PI);
        HolonomicDriveController holoController = new HolonomicDriveController(new PIDController(Constants.AutoConstants.kPXController, 0, 0), new PIDController(Constants.AutoConstants.kPYController, 0, 0), thetaController);

        SwerveControllerCommand nodeToAutobalanceCommand = new SwerveControllerCommand(startToAutobalance, s_Swerve::getPose, Constants.Swerve.swerveKinematics, holoController, () -> backwardsRelativeToDriver , s_Swerve::setModuleStates, s_Swerve);
        SwerveControllerCommand autoBalanceToOutsideCommunityCommand = new SwerveControllerCommand(autoBalanceToOutsideCommunity, s_Swerve::getPose, Constants.Swerve.swerveKinematics, holoController, () -> backwardsRelativeToDriver, s_Swerve::setModuleStates, s_Swerve);
        SwerveControllerCommand outsideCommunityToAutoBalanceCommand = new SwerveControllerCommand(outsideCommunityToAutoBalance, s_Swerve::getPose, Constants.Swerve.swerveKinematics, holoController, () -> backwardsRelativeToDriver, s_Swerve::setModuleStates, s_Swerve);

        initialRobotPos = new Pose2d(startToAutobalance.getInitialPose().getTranslation(), backwardsRelativeToDriver);
        addCommands(

                // wait 0.5 seconds to make sure no code is running in the background
                new WaitCommand(0.5),
                new ResetDriveOdometry(initialRobotPos, s_Swerve),
                // set positions out arm doesn't get stuck
                new SetAllPositions(s_Wrist, s_Elevator, s_Shoulder, Position.AUTOWRISTPOSITION, () -> GamePiece.CONE),
                new WaitCommand(0.25),

                // set pos to score
                new SetAllPositions(s_Wrist, s_Elevator, s_Shoulder, Position.CUBEHIGH, () -> GamePiece.CUBE),
                new IntakeOn(s_Intake, false),
                new WaitCommand(0.25),

                new SetAllPositions(s_Wrist, s_Elevator, s_Shoulder, Position.AUTOWRISTPOSITION, () -> GamePiece.CONE),
                new intakeStop(s_Intake),
               // new WaitCommand(0.25),

                // set position to stowed and stop the intake
                //new SetAllPositions(s_Wrist, s_Elevator, s_Shoulder, Position.STOWED, () -> GamePiece.CUBE),
                

                // autobalance
                nodeToAutobalanceCommand,
                autoBalanceToOutsideCommunityCommand,
                outsideCommunityToAutoBalanceCommand,
                new JustAutobalance(s_Swerve)
        );
    }

    public Pose2d getInitialTrajectoryPose(){
        return initialRobotPos;
    }
}