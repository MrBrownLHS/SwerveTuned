// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.RunCommand;

import frc.robot.commands.SwerveController;
import frc.robot.subsystems.SwerveSubsystem;
import frc.robot.utilities.constants.Constants;
import frc.robot.subsystems.CollectorArm;
import frc.robot.subsystems.CollectorArm.CollectorArmState;
import frc.robot.subsystems.CageClimber;
import frc.robot.commands.AutoCenterStart;
import frc.robot.commands.AutoLeftStart;
import frc.robot.commands.AutoRightStart;


public class RobotContainer {
  private final JoystickButton resetHeading;
  private final JoystickButton robotCentric;

  private final XboxController CoPilotController;
  private final CollectorArm collectorArm;
  private final CageClimber cageClimber;
  
  private final int translationAxis;
  private final int strafeAxis;
  private final int rotationAxis;
  
  
  private final Joystick DriverController;
  private final SwerveSubsystem swerveSubsystem;

  private final SendableChooser<Command> autoChooser;

  public RobotContainer() {
    swerveSubsystem = new SwerveSubsystem();
    DriverController = new Joystick(0);
    CoPilotController = new XboxController(1);
    collectorArm = new CollectorArm();
    cageClimber = new CageClimber();

     autoChooser = new SendableChooser<>();
        autoChooser.setDefaultOption("Center Start", new AutoCenterStart(swerveSubsystem, collectorArm));
        autoChooser.addOption("Left Start", new AutoLeftStart(swerveSubsystem, collectorArm));
        autoChooser.addOption("Right Start", new AutoRightStart(swerveSubsystem, collectorArm));

        SmartDashboard.putData("Auto Mode", autoChooser);
        
        

    resetHeading = new JoystickButton(DriverController, Constants.ControllerRawButtons.XboxController.Button.kY.value);
    robotCentric = new JoystickButton(DriverController, Constants.ControllerRawButtons.XboxController.Button.kX.value);

    translationAxis = Constants.ControllerRawButtons.XboxController.Axis.kLeftY.value;
    strafeAxis = Constants.ControllerRawButtons.XboxController.Axis.kLeftX.value;
    rotationAxis = Constants.ControllerRawButtons.XboxController.Axis.kRightX.value;
    
    swerveSubsystem.setDefaultCommand(new SwerveController(
            swerveSubsystem,
            () -> DriverController.getRawAxis(translationAxis) * 0.25,
            () -> DriverController.getRawAxis(strafeAxis) * 0.25,
            () -> -DriverController.getRawAxis(rotationAxis) * 0.25,
            () -> robotCentric.getAsBoolean()) // lambda probably not needed but why not
    );

    collectorArm.setDefaultCommand(
      new RunCommand(() -> {
        double articulationInput = CoPilotController.getRawAxis(XboxController.Axis.kRightY.value);
        double algaeInput = CoPilotController.getRawAxis(XboxController.Axis.kLeftX.value);
        double coralInput = CoPilotController.getRawAxis(XboxController.Axis.kLeftY.value);
        double yeetInput = CoPilotController.getRawAxis(XboxController.Axis.kRightTrigger.value);
    
        collectorArm.ArticulateCollector(() -> articulationInput);
        collectorArm.CollectAlgae(() -> algaeInput);
        collectorArm.CollectCoral(() -> coralInput);
        collectorArm.YeetAlgae(() -> yeetInput);
    
      }, collectorArm)
    );
  configureBindings(); 
  }

  private void configureBindings() {
    resetHeading.whileTrue(new InstantCommand(() -> swerveSubsystem.resetHeading()));

    new JoystickButton(CoPilotController, XboxController.Button.kA.value)
    .onTrue(new InstantCommand(() -> collectorArm.moveToState(CollectorArmState.PROCESSOR), collectorArm));

    new JoystickButton(CoPilotController, XboxController.Button.kB.value)
    .onTrue(new InstantCommand(() -> collectorArm.moveToState(CollectorArmState.COLLECT), collectorArm)); 

    new JoystickButton(CoPilotController, XboxController.Button.kX.value)
    .onTrue(new InstantCommand(() -> collectorArm.moveToState(CollectorArmState.L1), collectorArm));

    new JoystickButton(CoPilotController, XboxController.Button.kY.value)
    .onTrue(new InstantCommand(() -> collectorArm.moveToState(CollectorArmState.L2), collectorArm));

    new JoystickButton(CoPilotController, XboxController.Button.kLeftBumper.value)
    .onTrue(new InstantCommand(() -> collectorArm.moveToState(CollectorArmState.L3), collectorArm));

    new JoystickButton(CoPilotController, XboxController.Button.kRightBumper.value)
    .onTrue(new InstantCommand(() -> collectorArm.moveToState(CollectorArmState.MAX), collectorArm));

    new JoystickButton(CoPilotController, Constants.ControllerRawButtons.DPAD_EAST)
    .whileTrue(new InstantCommand(() -> cageClimber.ReadyCageGrabber(), cageClimber));

    new JoystickButton(CoPilotController, Constants.ControllerRawButtons.DPAD_WEST)
    .whileTrue(new InstantCommand(() -> cageClimber.CageClimb(), cageClimber));

    new JoystickButton(CoPilotController, Constants.ControllerRawButtons.DPAD_SOUTH)
    .onTrue(new InstantCommand(() -> cageClimber.CageClimbStop(), cageClimber));

    new JoystickButton(CoPilotController, XboxController.Button.kStart.value)
    .onTrue(new InstantCommand(collectorArm::stopArm, collectorArm));




    

  
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}
