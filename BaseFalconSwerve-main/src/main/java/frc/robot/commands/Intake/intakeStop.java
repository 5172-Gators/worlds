package frc.robot.commands.Intake;


import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IntakeSub;


public class intakeStop extends CommandBase {
    private IntakeSub s_Intake;
   

    public intakeStop(IntakeSub s_Intake) {
        this.s_Intake = s_Intake;
             addRequirements(s_Intake);
    }

    @Override
    public void initialize() {
        s_Intake.setMotor(0);
    }

    @Override
    public void execute() {
        s_Intake.setMotor(0);
    }

    public void end(boolean Interrupted){
        System.out.println("stopped intaking.");
    }

    @Override
    public boolean isFinished() {

      return true;
    }
    
}