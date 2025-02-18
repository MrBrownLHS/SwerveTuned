// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CollectorArm;
import frc.robot.subsystems.CollectorArm.CollectorArmState;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class MoveCollectorArm extends Command {
    private final CollectorArm collectorArm;
    private final CollectorArmState targetState;
  
  public MoveCollectorArm(CollectorArm collectorArm, CollectorArmState state) {
    this.collectorArm = collectorArm;
    this.targetState = state;
    addRequirements(collectorArm);
    
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    collectorArm.moveToState(targetState);}


  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return collectorArm.isAtTarget(targetState);
}
}
