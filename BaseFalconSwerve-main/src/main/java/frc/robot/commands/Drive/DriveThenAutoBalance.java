// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.Drive;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Swerve;

public class DriveThenAutoBalance extends CommandBase {
  /** Creates a new TeleopElevatorTest. */
  private Swerve s_Swerve;

  private Timer timer;

  private double startTime;

  public DriveThenAutoBalance(Swerve s_Swerve) {
    // Use addRequirements() here to declare subsystem dependencies.
    this.s_Swerve = s_Swerve;
    startTime = 0;
    timer = new Timer();
    addRequirements(s_Swerve);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    timer.reset();
    timer.start();
    startTime = Timer.getFPGATimestamp();
      System.out.println("auto balancing...");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
   public void execute() {
      
      if(Timer.getFPGATimestamp() < startTime + 4 ){
        s_Swerve.drive(new Translation2d(-0.65,0), 0, false, false);
      } else {
        s_Swerve.drive(new Translation2d(s_Swerve.GetRoll() / 35,0), 0, false, false);
      } // roll is divided by 45

  }
  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
      timer.stop();
      timer.reset();
      System.out.println("auto balance finished.");
      s_Swerve.drive(new Translation2d(0, 0), 0, false, false);
    }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
      return timer.hasElapsed(10) ||  !DriverStation.isAutonomousEnabled();
    
  }
}



