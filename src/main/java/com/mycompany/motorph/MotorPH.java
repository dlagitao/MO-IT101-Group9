/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.motorph;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.LocalTime;
import java.time.Duration;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * ==============================================================
 * Contains all primary variables and data for use in the system.
 * Handles CSV data reading, employee details presentation,
 * and payroll and hours worked calculation
 * from June to December 2024.
 * 
 * @author Cail Maven Lusares
 * @author Dominic Gideon Remetio Abad
 * @author Dominic Lagitao
 * @author Luis Carlo Dayag
 * @author Marlu Gonzales
 * @version 1.0
 * ==============================================================
 */

public class MotorPH {
    /**
     * ==============================================================
     * Starts the MotorPH Payroll System by loading employee and 
     * DTR data from CSV files, computing daily hours worked, 
     * and then directing the user to either the Employee or 
     * Payroll Staff Portal after logging in.
     *
     * @param args command-line arguments (not used).
     * @throws     FileNotFoundException if the employee or DTR 
     *             CSV files are not found in the specified directory.
     *
     * ==============================================================
     */
    public static void main(String[] args) throws FileNotFoundException {
        class Employee {
            // Employee information.
            static ArrayList<String> employeeNumber   = new ArrayList<>();
            static ArrayList<String> lastName   = new ArrayList<>();
            static ArrayList<String> firstName  = new ArrayList<>();
            static ArrayList<String> birthDay   = new ArrayList<>();
            static ArrayList<String> basePay   = new ArrayList<>();
            static ArrayList<String> hourlyRate   = new ArrayList<>();
        }

        class EmployeeDTRData {
            // Daily Time Record (DTR) data.
            static ArrayList<String>    dtrEmployeeNumber  = new ArrayList<>();
            static ArrayList<String>    dtrDate  = new ArrayList<>();
            static ArrayList<String>    timeIn  = new ArrayList<>();
            static ArrayList<String>    timeOut  = new ArrayList<>();
            static ArrayList<LocalTime>    parsedIn  = new ArrayList<>();
            static ArrayList<LocalTime>    parsedOut  = new ArrayList<>();
            static ArrayList<Double>    dailyHours  = new ArrayList<>();
        }
        
    
        loadEmployees("resources/motorphemployeedata.csv", Employee.employeeNumber, Employee.lastName, Employee.firstName, Employee.birthDay, Employee.basePay, Employee.hourlyRate);
        loadDTR("resources/loginandout.csv", EmployeeDTRData.dtrEmployeeNumber, EmployeeDTRData.dtrDate, EmployeeDTRData.timeIn, EmployeeDTRData.timeOut, EmployeeDTRData.dailyHours, 
                                             EmployeeDTRData.parsedIn, EmployeeDTRData.parsedOut);
        computeDailyHours(EmployeeDTRData.timeIn, EmployeeDTRData.timeOut, EmployeeDTRData.parsedIn, EmployeeDTRData.parsedOut, EmployeeDTRData.dailyHours);


        Scanner scanner = new Scanner(System.in);

        boolean loggedIn = false;

        // Loop continues until valid credentials are entered.
        // Prevents access to the system without authentication.
        while(!loggedIn) {
            System.out.print("Username: ");
            String userName = scanner.nextLine();
            System.out.print("Password: ");
            String passWord = scanner.nextLine();

             if ("employee".equals(userName) && "12345".equals(passWord)) {
                runEmployeePortal(scanner, Employee.employeeNumber, Employee.lastName, Employee.firstName, Employee.birthDay);
                loggedIn = true;
            } else if ("payroll_staff".equals(userName) && "12345".equals(passWord)) {
                runPayrollPortal(scanner, Employee.employeeNumber, Employee.firstName, Employee.lastName, Employee.birthDay, Employee.hourlyRate, Employee.basePay, EmployeeDTRData.timeIn, EmployeeDTRData.timeOut, EmployeeDTRData.dtrEmployeeNumber, EmployeeDTRData.dtrDate, EmployeeDTRData.dailyHours);
                loggedIn = true;
            } else {
                System.out.println("Incorrect username and/or password. Please try again.\n");
            }
        }
    }
    
    /**
     * ==============================================================
     * Displays the Employee Portal, allowing the user to either view 
     * employee details or exit the system.
     * No access to payroll processing.
     * 
     * @param scanner        Scanner object used to read user input.
     * @param employeeNumber List of employee ID numbers.
     * @param lastName       List of employee last names.
     * @param firstName      List of employee first names.
     * @param birthDay       List of employee birthdays.
     * ==============================================================
     */
    static void runEmployeePortal(Scanner scanner, ArrayList<String> employeeNumber, ArrayList<String> lastName, ArrayList<String> firstName, ArrayList<String> birthDay) {
        while (true) {
            System.out.println("\n---- Employee Portal ----");
            System.out.println("[1] Check Employee Profile");
            System.out.println("[2] Exit");
            String choice = scanner.nextLine().trim();

            if ("1".equals(choice)) {
                System.out.print("Enter your Employee No.: ");
                String num = scanner.nextLine().trim();
                boolean hit = false;
                for (int index = 0; index < employeeNumber.size(); index++) {
                    if (num.equals(employeeNumber.get(index))) {
                        displayEmployeeDetails(index, employeeNumber, firstName, lastName, birthDay);
                        hit = true;
                        break;
                    }
                }
                if (!hit) System.out.println("Employee number does not exist. Please check the number and try again.");

            } else if ("2".equals(choice)) {
                System.out.println("Exiting payroll system.");
                scanner.close();
                System.exit(0); 
            } else {
                System.out.println("Please enter 1 or 2 only.");
            }
        }
    }

    /**
     * ==============================================================
     * Displays the Payroll Staff Portal where the user can choose to 
     * either generate a payslip or exit the system.
     * 
     * @param scanner           Scanner object used to read user input.
     * @param employeeNumber    List of employee ID numbers.
     * @param firstName         List of employee first names.
     * @param lastName          List of employee last names.
     * @param birthDay          List of employee birthdays.
     * @param hourlyRate        List of employee hourly rates.
     * @param basePay           List of employee monthly base salaries.
     * @param timeIn            List of recorded clock-in times.
     * @param timeOut           List of recorded clock-out times.
     * @param dtrEmployeeNumber List of employee IDs from DTR records.
     * @param dtrDate           List of dates from DTR records.
     * @param dailyHours        List of pre-computed daily work hours.
     * ==============================================================
     */
    static void runPayrollPortal(Scanner scanner, ArrayList<String> employeeNumber, ArrayList<String> firstName, ArrayList<String> lastName, ArrayList<String> birthDay,ArrayList<String> hourlyRate, 
            ArrayList<String> basePay, ArrayList<String> timeIn, ArrayList<String> timeOut, ArrayList<String> dtrEmployeeNumber, ArrayList<String> dtrDate, ArrayList<Double> dailyHours) {
        while (true) {
            System.out.println("\n---- Payroll Portal ----");
            System.out.println("[1] Generate Payslip");
            System.out.println("[2] Exit");
            String choice = scanner.nextLine().trim();

            if ("1".equals(choice)) {
                runPayslipMenu(scanner, employeeNumber, firstName, lastName, birthDay, hourlyRate, basePay, timeIn, timeOut, dtrEmployeeNumber, dtrDate, dailyHours);
            } else if ("2".equals(choice)) {
                System.out.println("Exiting payroll system.");
                scanner.close();
                System.exit(0);
            } else {
                System.out.println("Please enter 1 or 2 only.");
            }
        }
    }

    /**
     * ==============================================================
     * Displays the payslip processing menu where the user can 
     * choose to either process payroll for a single employee, 
     * all employees, or exit the system.
     * 
     * @param scanner           Scanner object used to read user input.
     * @param employeeNumber    List of employee ID numbers.
     * @param firstName         List of employee first names.
     * @param lastName          List of employee last names.
     * @param birthDay          List of employee birthdays.
     * @param hourlyRate        List of employee hourly rates.
     * @param basePay           List of employee monthly base salaries.
     * @param timeIn            List of recorded clock-in times.
     * @param timeOut           List of recorded clock-out times.
     * @param dtrEmployeeNumber List of employee IDs from DTR records.
     * @param dtrDate           List of dates from DTR records.
     * @param dailyHours        List of pre-computed daily work hours.
     * ==============================================================
     */
    static void runPayslipMenu(Scanner scanner, ArrayList<String> employeeNumber, ArrayList<String> firstName, ArrayList<String> lastName, ArrayList<String> birthDay,ArrayList<String> hourlyRate, 
            ArrayList<String> basePay, ArrayList<String> timeIn, ArrayList<String> timeOut, ArrayList<String> dtrEmployeeNumber, ArrayList<String> dtrDate, ArrayList<Double> dailyHours) {
        while (true) {
            System.out.println("\n---- Generate Payslip ----");
            System.out.println("[1] Single Employee");
            System.out.println("[2] All Employees");
            System.out.println("[3] Exit");
            String choice = scanner.nextLine().trim();

            if ("1".equals(choice)) {
                processSingleEmployee(scanner, employeeNumber, firstName, lastName, birthDay, hourlyRate, basePay, timeIn, timeOut, dtrEmployeeNumber, dtrDate, dailyHours);
            } else if ("2".equals(choice)) {
                processAllEmployees(scanner, employeeNumber, firstName, lastName, birthDay, hourlyRate, basePay, timeIn, timeOut, dtrEmployeeNumber, dtrDate, dailyHours);
            } else if ("3".equals(choice)) {
                System.out.println("Exiting payroll system.");
                scanner.close();
                System.exit(0);
            } else {
                System.out.println("Please enter 1, 2, or 3 only.");
            }
        }
    }

    /**
     * ==============================================================
     * Combines all calculation methods for payroll processing,
     * including hours worked, late deductions, SSS,
     * PhilHealth, Pag-IBIG, withholding tax, and overall net pay.
     * Calculations are performed based on employee ID, 
     * hourly rate, and base pay, and the results are passed to the 
     * displayPayrollCalculations method for displaying.
     *
     * @param num               Employee ID as a String.
     * @param index             Index of the employee in the employee data list.
     * @param month             Month variable for the display label.
     * @param employeeNumber    List of employee ID numbers.
     * @param hourlyRate        List of employee hourly rates.
     * @param basePay           List of employee monthly base salaries.
     * @param timeIn            List of recorded clock-in times.
     * @param timeOut           List of recorded clock-out times.
     * @param dtrEmployeeNumber List of employee IDs from DTR records.
     * @param dtrDate           List of dates from DTR records.
     * @param dailyHours        List of pre-computed daily work hours.
     * ==============================================================
     */
    
    static void processPayroll(String num, int index, int month, ArrayList<String> employeeNumber, ArrayList<String> hourlyRate, ArrayList<String> basePay, ArrayList<String> timeIn, ArrayList<String> timeOut, ArrayList<String>dtrEmployeeNumber, ArrayList<String>dtrDate, ArrayList<Double> dailyHours) {
        num  = employeeNumber.get(index);
        double rate = Double.parseDouble(hourlyRate.get(index));
        double base = Double.parseDouble(basePay.get(index));
        double hrs1   = getHours(num, "first",  month, timeIn, timeOut, dtrEmployeeNumber, dtrDate, dailyHours);
        double hrs2   = getHours(num, "second", month, timeIn, timeOut, dtrEmployeeNumber, dtrDate, dailyHours);

        // Payroll is split into two cutoffs.
        double gross1 = hrs1 * rate;
        double gross2 = hrs2 * rate;
        double combinedGross   = gross1 + gross2; // Total gross pay for the entire month (two cutoffs).
        
        double sss        = sssTable(combinedGross); // Computed from monthly earnings, not per cutoff salary.
        double philHealth = philhealthShare(combinedGross); 
        double pagIbig    = pagibigShare(base); // Mandatory to use the base pay, not the gross pay, based on Pag-IBIG website. Capped at 100PHP monthly.
        double taxable    = combinedGross - sss - philHealth - pagIbig; // Calculates taxable income after applying all statutory deductions (excluding tax).
        double whTax      = withholdingTax(taxable); // Uses progressive tax system: base tax + % of excess over the bracket threshold.
        double totalDed   = sss + philHealth + pagIbig + whTax;

        double net1 = gross1; // First cutoff comprises of earnings only, no government deductions applied, as per company payroll distribution policy.
        double net2 = gross2 - totalDed; // Only second cutoff applies government deducations (SSS, PhilHealth, Pag-IBIG, tax), as per company payroll distribution policy.

        String monthLabel   = monthLabel(month);
        int lastDay = YearMonth.of(2024, month).lengthOfMonth();
        double cap = (lastDay == 31) ? 88.0 : 80.0; // 31-month days are given an 11-day work week (11 X 8 = 88 hours); otherwise, the cap is kept within a 10-day work week (10 X 8 = 80).

        displayPayrollCalculations(monthLabel, lastDay, hrs1, gross1, net1, hrs2, cap, gross2, sss, philHealth, pagIbig, whTax, totalDed, net2);
    }
    /**
     * ==============================================================
     * Method used to display the calculation results of processPayroll.
     * Results are displayed by month and cutoff period.
     * 
     * @param monthLabel The corresponding month of each calculation
     * @param lastDay    The last day of the corresponding month.
     * @param hrs1       Hours worked of the first cutoff.
     * @param gross1     Gross salary of the first cutoff.
     * @param net1       Net salary of the first cutoff, which will be lower 
     *                   if the employee had late days.
     * @param hrs2       Hours worked of the second cutoff.
     * @param cap        Maximum working hours for the given month.
     * @param gross2     Gross salary of the second cutoff.
     *                   For more information on these gov't deductions,
     *                   refer to their methods later in the code.
     * @param sss        SSS deduction for the employee.
     * @param philHealth PhilHealth deduction for the employee.
     * @param pagIbig    Pag-IBIG deduction for the employee.
     * @param whTax      Employee's withholding tax.
     * @param totalDed   Total gov't deductions.
     * @param net2       Net salary of the second cutoff.
     * ==============================================================
     */
    static void displayPayrollCalculations(String monthLabel, int lastDay, double hrs1, double gross1, double net1, double hrs2, double cap, double gross2, double sss, double philHealth, double pagIbig, double whTax, double totalDed, double net2) {
        System.out.println("\n========================================");
        System.out.println("  Cutoff: " + monthLabel + " 1 to " + monthLabel + " 15");
        System.out.println("  (8 hrs/day x 5 days/wk x 2 wks = 80 hrs max)");
        System.out.println("========================================");
        System.out.println("Total Hours Worked : " + hrs1 + " / 80.00 hrs");
        System.out.println("Gross Salary       : Php" +  gross1);
        System.out.println("Net Salary         : Php" + net1);
        System.out.println("\n========================================");
        System.out.println("  Cutoff: " + monthLabel + " 16 to " + monthLabel + " " + lastDay);
        System.out.println("  (8 hrs/day x 5 days/wk x 2 wks = " + cap + " hrs max)");
        System.out.println("========================================");
        System.out.println("Total Hours Worked : " + hrs2 + " / " + cap + " hrs");
        System.out.println("Gross Salary       : Php" + gross2);
        System.out.println("\n--- Statutory Deductions ---");
        System.out.println("  SSS             : Php" + sss);
        System.out.println("  PhilHealth      : Php" + philHealth);
        System.out.println("  Pag-IBIG        : Php" + pagIbig);
        System.out.println("  Tax             : Php" + whTax);
        System.out.println("  Subtotal        : Php" + totalDed);
        System.out.println("Net Salary        : Php" + net2);
        System.out.println("========================================");
    }

    /**
     * ==============================================================
     * This method displays employee details.
     * It takes the index as input,
     * and then displays the ID, full name, 
     * and birthday at the given index.
     * 
     * @param index          Index variable that describes the row in which
     *                       the correct information can be found.
     * @param employeeNumber List of employee ID numbers.
     * @param firstName      List of employee first names.
     * @param lastName       List of employee last names.
     * @param birthDay       List of employee birthdays.
     * ==============================================================
     */
    static void displayEmployeeDetails(int index, ArrayList<String> employeeNumber, ArrayList<String> firstName, ArrayList<String> lastName, ArrayList<String> birthDay) {
        System.out.println("\n----Employee Details----");
        System.out.println("Employee #   : " + employeeNumber.get(index));
        System.out.println("Employee Name: " + firstName.get(index) + " " + lastName.get(index));
        System.out.println("Birthday     : " + birthDay.get(index));
    }
   
    
    /**
     * ==============================================================
     * Processes payroll for a single employee.
     * The method first requests for the Employee ID.
     * It then displays the employee ID, full name,
     * and birthday before calling the processPayroll method.
     * 
     * @param scanner           Scanner object used to read user input.
     * @param employeeNumber    List of employee ID numbers.
     * @param firstName         List of employee first names.
     * @param lastName          List of employee last names.
     * @param birthDay          List of employee birthdays.
     * @param hourlyRate        List of employee hourly rates.
     * @param basePay           List of employee monthly base salaries.
     * @param timeIn            List of recorded clock-in times.
     * @param timeOut           List of recorded clock-out times.
     * @param dtrEmployeeNumber List of employee IDs from DTR records.
     * @param dtrDate           List of dates from DTR records.
     * @param dailyHours        List of pre-computed daily work hours.
     * ==============================================================
     */
    static void processSingleEmployee(Scanner scanner, ArrayList<String> employeeNumber, ArrayList<String> firstName, ArrayList<String> lastName, 
                                      ArrayList<String> birthDay, ArrayList<String> hourlyRate, ArrayList<String> basePay, ArrayList<String> timeIn, ArrayList<String> timeOut, ArrayList<String> dtrEmployeeNumber, ArrayList<String> dtrDate, ArrayList<Double> dailyHours) {
        System.out.print("Employee No.: ");
        String num = scanner.nextLine().trim();
        
        int index = employeeNumber.indexOf(num);
        
        if (index == -1) { 
            System.out.println("Employee number does not exist."); return; 
        }
        
        displayEmployeeDetails(index, employeeNumber, firstName, lastName, birthDay);
                
        for (int month = 6; month <= 12; month++) {
            processPayroll(num, index, month, employeeNumber, hourlyRate, basePay, timeIn, timeOut, dtrEmployeeNumber, dtrDate, dailyHours);
        }
    }
    
    /**
     * ==============================================================
     * Similar to processSingleEmployee(), 
     * but processes payroll for all employees (bulk).
     *
     * @param scanner           Scanner object used to read user input.
     * @param employeeNumber    List of employee ID numbers.
     * @param firstName         List of employee first names.
     * @param lastName          List of employee last names.
     * @param birthDay          List of employee birthdays.
     * @param hourlyRate        List of employee hourly rates.
     * @param basePay           List of employee monthly base salaries.
     * @param timeIn            List of recorded clock-in times.
     * @param timeOut           List of recorded clock-out times.
     * @param dtrEmployeeNumber List of employee IDs from DTR records.
     * @param dtrDate           List of dates from DTR records.
     * @param dailyHours        List of pre-computed daily work hours.
     * ==============================================================
     */
    static void processAllEmployees(Scanner scanner, ArrayList<String> employeeNumber, ArrayList<String> firstName, ArrayList<String> lastName, 
                                    ArrayList<String> birthDay, ArrayList<String> hourlyRate, ArrayList<String> basePay, ArrayList<String> timeIn, ArrayList<String> timeOut, ArrayList<String> dtrEmployeeNumber, ArrayList<String> dtrDate, ArrayList<Double> dailyHours) {
        // Loops from June to December.
        for (int index = 0; index < employeeNumber.size(); index++) {
            String num = employeeNumber.get(index);
            for (int month = 6; month <= 12; month++) {
                displayEmployeeDetails(index, employeeNumber, firstName, lastName, birthDay);
                processPayroll(num, index, month, employeeNumber, hourlyRate, basePay, timeIn, timeOut, dtrEmployeeNumber, dtrDate, dailyHours);
            }
        }
    }

    /**
     * ==============================================================
     * Loads the employee master list from a CSV file.
     * Stores employee IDs, last names, first names, birthdays, 
     * base pays, and hourly rates.
     * 
     * @param path           The file pathname to the CSV file.
     * @param employeeNumber List to store employee IDs.
     * @param lastName       List to store last names.
     * @param firstName      List to store first names.
     * @param birthDay       List to store birthdays.
     * @param basePay        List to store monthly base salaries.
     * @param hourlyRate     List to store hourly rates.
     * @throws               FileNotFoundException if the CSV file is 
     *                       not found at the specified path.
     * ==============================================================
     */
    static void loadEmployees(String path, ArrayList<String> employeeNumber, ArrayList<String> lastName, 
                              ArrayList<String> firstName, ArrayList<String> birthDay, ArrayList<String> basePay, ArrayList<String> hourlyRate) {
        try {
            Scanner reader = new Scanner(new FileReader(path));
            if (reader.hasNextLine()) reader.nextLine(); // Skips the header row.
            while (reader.hasNextLine()) {
                // Splits the CSV line into columns.
                String[] column = splitCSV(reader.nextLine());
                employeeNumber.add(column[0]);
                lastName.add(column[1]);
                firstName.add(column[2]);
                birthDay.add(column[3]);
                basePay.add(column[13].replace(",", ""));
                hourlyRate.add(column[18].replace(",", ""));
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open employee file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * ==============================================================
     * Loads DTR data from a CSV file (Log-in and logout times).
     *
     * Stores employee IDs, dates, time-ins, and time-outs.
     * 
     * @param path              The file pathname to the CSV file
     * @param dtrEmployeeNumber List to store employee IDs from DTR.
     * @param dtrDate           List to store dates from DTR.
     * @param timeIn            List to store recorded clock-in times.
     * @param timeOut           List to store recorded clock-out times.
     * @param dailyHours        List to store pre-computed daily work hours.
     * @param parsedIn          List to store parsed clock-in times.
     * @param parsedOut         List to store parsed clock-out times.
     * @throws                  FileNotFoundException if the CSV file is 
     *                          not found at the specified path.
     * ==============================================================
     */
    static void loadDTR(String path, ArrayList<String> dtrEmployeeNumber, ArrayList<String> dtrDate, 
                        ArrayList<String> timeIn, ArrayList<String> timeOut, ArrayList<Double> dailyHours, ArrayList<LocalTime> parsedIn, ArrayList<LocalTime> parsedOut) {
        try {
            Scanner reader = new Scanner(new FileReader(path));
            if (reader.hasNextLine()) reader.nextLine(); // Skips the header row.
            while (reader.hasNextLine()) {
                // Splits the CSV line into columns.
                String[] column   = splitCSV(reader.nextLine());
                dtrEmployeeNumber.add(column[0]);
                dtrDate.add(column[3]);
                timeIn.add(column[4]);
                timeOut.add(column[5]);
                dailyHours.add(0.0); 
                parsedIn.add(null);  
                parsedOut.add(null);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Could not open DTR file: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * ==============================================================
     * Computes daily hours worked for all employees in advance.
     * This method pre-computes daily hours to avoid recalculation
     * during payslip generation.
     * 
     * Rules:
     * - Arrival at or before 8:10 AM is considered on time.
     * - Working hours are capped at 8 hours per day for those on time.
     * - No overtime is counted for hours worked past 5:00 PM.
     * - A 1-hour lunch break is deducted in the logic.
     *
     * @param timeIn     List of recorded clock-in times.
     * @param timeOut    List of recorded clock-out times.
     * @param parsedIn   List of parsed clock-in times.
     * @param parsedOut  List of parsed clock-out times.
     * @param dailyHours List to store pre-computed daily work hours.
     * ==============================================================
     */
    static void computeDailyHours(ArrayList<String> timeIn, ArrayList<String> timeOut, ArrayList<LocalTime> parsedIn, ArrayList<LocalTime> parsedOut, ArrayList<Double> dailyHours) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("H:mm", Locale.ENGLISH);
        LocalTime graceEnd   = LocalTime.of(8, 10);
        LocalTime workStart  = LocalTime.of(8,  0);
        LocalTime workEnd    = LocalTime.of(17, 0);

        for (int index = 0; index < timeIn.size(); index++) {
            // Skip if time-in or time-out is missing.
            if (timeIn.get(index) == null || timeOut.get(index) == null) continue;

            try {
                // Parse time-in and time-out strings into LocalTime.
                LocalTime login  = LocalTime.parse(timeIn.get(index).trim(),  format);
                LocalTime logout = LocalTime.parse(timeOut.get(index).trim(), format);
                
                parsedIn.set(index,  login);
                parsedOut.set(index, logout);
                
                // Caps logout at 5:00 PM — no overtime counted.
                if (logout.isAfter(workEnd)) logout = workEnd;
                
                LocalTime actualStart;
                if (login.isBefore(workStart)) {           // If employees arrive before 8:00 AM, this sets login time as the actual time.
                    actualStart = login;
                } else if (!login.isAfter(graceEnd)) {     // Applies 10-minute grace period (UP TO 8:10 AM). Employees logging in between 8:00–8:10 are treated as on-time.
                    actualStart = workStart;              
                } else {                                   // Logins after 8:10 are considered late and use actual login time.
                    actualStart = login;
                }
                
                // Computes minutes worked, then deducts 1-hour lunch.
                long totalMinutes = Duration.between(actualStart, logout).toMinutes();
                
                // Deducts the 1-hour lunch ONLY if logged duration is more than 1 hour.
                totalMinutes = (totalMinutes > 60) ? totalMinutes - 60 : 0; 
                
                double hoursWorked = Math.min(totalMinutes / 60.0, 8.0);

                // Caps at 8 hours and saves.
                dailyHours.set(index, Math.max(0.0, hoursWorked));
                
            } catch (Exception e) {
                dailyHours.set(index, 0.0);
            }
        }
    }
    
    /**
     * ==============================================================
     * Calculates total hours worked by an employee for a specific 
     * month and cutoff period because payroll is processed twice a month.
     *
     * This is used to compute gross pay.
     *
     * @param num               The employee ID number as a String that must match 
     *                          an entry in the dtrEmployeeNumber array.
     * @param half              The cutoff period; must be either "first" (days 1-15) 
     *                          or "second" (days 16-end).
     * @param month             June to December, as an integer (6-12).
     * @param timeIn            List of recorded clock-in times.
     * @param timeOut           List of recorded clock-out times.
     * @param dtrEmployeeNumber List of employee ID numbers from DTR.
     * @param dtrDate           List of dates from DTR records.
     * @param dailyHours        List of pre-computed daily work hours.
     * @return                  The total hours worked, capped at the maximum 
     *                          standard hours for the period.
     * ==============================================================
     */
    static double getHours(String num, String half, int month, ArrayList<String> timeIn, ArrayList<String> timeOut, ArrayList<String> dtrEmployeeNumber, ArrayList<String> dtrDate, ArrayList<Double> dailyHours) {
        double total = 0;
        for (int index = 0; index < timeIn.size(); index++) {
            if (!num.equals(dtrEmployeeNumber.get(index))) continue;
            if (dtrDate.get(index) == null) continue;
            try {
                String[] date = dtrDate.get(index).trim().split("/");
                int currentMonth   = Integer.parseInt(date[0]);
                int day = Integer.parseInt(date[1]);
                if (currentMonth != month) continue;
                // Add hours for the selected cutoff period.
                if ("first".equals(half)  && day >= 1  && day <= 15) total += dailyHours.get(index);
                if ("second".equals(half) && day >= 16 && day <= 31) total += dailyHours.get(index);
            } catch (Exception ignored) {}
        }
        // Cap to max working hours for the period.
        int lastDay = YearMonth.of(2024, month).lengthOfMonth();
        
        // First cutoff (days 1–15) assumes 10 workdays (Mon–Fri x 2 weeks).
        // Second cutoff (days 16–end) may have 11 workdays if the month has 31 days,
        // which means an extra weekday in longer months.
        int maxDays = "first".equals(half) ? 10 : (lastDay == 31 ? 11 : 10);
        return Math.min(total, maxDays * 8.0); // Caps total hours to avoid exceeding standard working hours.
                                               // (8 hours/day × number of working days in the cutoff period).
    }

    /**
     * ==============================================================
     * Calculates the employee's SSS contribution based on the gross salary.
     * The contribution is determined using fixed salary brackets. 
     * The method returns the corresponding SSS contribution 
     * for the employee's salary bracket.
     * 
     * @param gross The employee's gross monthly salary.
     * @return      The corresponding SSS contribution.
     * ==============================================================
     */
    static double sssTable(double gross) {
        if (gross < 3250)   return 135.00;
        if (gross <= 3750)  return 157.50;
        if (gross <= 4250)  return 180.00;
        if (gross <= 4750)  return 202.50;
        if (gross <= 5250)  return 225.00;
        if (gross <= 5750)  return 247.50;
        if (gross <= 6250)  return 270.00;
        if (gross <= 6750)  return 292.50;
        if (gross <= 7250)  return 315.00;
        if (gross <= 7750)  return 337.50;
        if (gross <= 8250)  return 360.00;
        if (gross <= 8750)  return 382.50;
        if (gross <= 9250)  return 405.00;
        if (gross <= 9750)  return 427.50;
        if (gross <= 10250) return 450.00;
        if (gross <= 10750) return 472.50;
        if (gross <= 11250) return 495.00;
        if (gross <= 11750) return 517.50;
        if (gross <= 12250) return 540.00;
        if (gross <= 12750) return 562.50;
        if (gross <= 13250) return 585.00;
        if (gross <= 13750) return 607.50;
        if (gross <= 14250) return 630.00;
        if (gross <= 14750) return 652.50;
        if (gross <= 15250) return 675.00;
        if (gross <= 15750) return 697.50;
        if (gross <= 16250) return 720.00;
        if (gross <= 16750) return 742.50;
        if (gross <= 17250) return 765.00;
        if (gross <= 17750) return 787.50;
        if (gross <= 18250) return 810.00;
        if (gross <= 18750) return 832.50;
        if (gross <= 19250) return 855.00;
        if (gross <= 19750) return 877.50;
        if (gross <= 20250) return 900.00;
        if (gross <= 20750) return 922.50;
        if (gross <= 21250) return 945.00;
        if (gross <= 21750) return 967.50;
        if (gross <= 22250) return 990.00;
        if (gross <= 22750) return 1012.50;
        if (gross <= 23250) return 1035.00;
        if (gross <= 23750) return 1057.50;
        if (gross <= 24250) return 1080.00;
        if (gross <= 24750) return 1102.50;
        return 1125.00;
    }

    /**
     * ==============================================================
     * Calculates the employee's share of the PhilHealth premium.
     * Rules:
     * - The total premium is 3% of gross income, and is split 50/50 
     * between employee and employer.
     * - If the gross is less than or equal to 10,000, premium is 300.
     * - If the gross is greater than or equal to 60,000, premium is capped at 1,800.
     * 
     * @param gross The employee's gross monthly salary.
     * @return      The employee's share of the PhilHealth premium
     *              (50% of the total premium).
     * *==============================================================
     */
    static double philhealthShare(double gross) {
        double premium;
        if (gross <= 10000)      premium = 300.00;
        else if (gross >= 60000) premium = 1800.00;
        else                     premium = gross * 0.03;
        return premium * 0.50;
    }

     /**
     * ==============================================================
     * Calculates the employee's share of Pag-IBIG contributions.
     * Rules:
     * - Contribution is 1% of the base pay if the base pay is less than or equal to 1,500.
     * - Contribution is 2% of the base pay if the base pay is greater than 1,500.
     * - The contribution is capped at 100.
     * 
     * @param base The employee's base salary.
     * @return     The Pag-IBIG contribution, capped at 100.
     * ==============================================================
     */
    static double pagibigShare(double base) {
        double contribution = (base <= 1500) ? base * 0.01 : base * 0.02;
        return Math.min(contribution, 100.00);
    }

    /**
     * ==============================================================
     * Calculates the employee's withholding tax based on taxable income.
     * Each salary bracket has a fixed base tax plus a 
     * percentage of income above the bracket threshold.
     * @param taxable The employee's taxable income.
     * @return        The corresponding withholding tax.
     * * ==============================================================
     */
    static double withholdingTax(double taxable) {
        if (taxable <= 20832)  return 0;
        if (taxable <= 33332)  return (taxable - 20833) * 0.20;
        if (taxable <= 66666)  return 2500 + (taxable - 33333) * 0.25;
        if (taxable <= 166666) return 10833 + (taxable - 66667) * 0.30;
        if (taxable <= 666666) return 40833.33 + (taxable - 166667) * 0.32;
        return 200833.33 + (taxable - 666666) * 0.35;
    }

    /**
     * ==============================================================
     * Converts a month number to its corresponding month name.
     * @param   month The month number (6-12)
     * @return  Month name as a string, or "???" if invalid.
     * * ==============================================================
     */
    static String monthLabel(int month) {
        switch (month) {
            case 6: return "June";      case 7: return "July";
            case 8: return "August";    case 9: return "September";
            case 10: return "October";  case 11: return "November";
            case 12: return "December"; default: return "???";
        }
    }

    /**
     * ==============================================================
     * Splits a CSV line into an array of strings.
     * Handles quoted fields; commas inside quotes are ignored.
     * Leading and trailing spaces are trimmed from each field.
     * @param line The CSV line to split.
     * @return columns An array of strings representing each column in the CSV line.
     * * ==============================================================
     */

    // Custom CSV parser is used because some fields may contain commas inside quotes,
    // which would break a simple split(",") approach.
    static String[] splitCSV(String line) {
        String[] columns = new String[19]; // Stores parsed CSV fields.
        boolean quoted = false; 
        StringBuilder buffer = new StringBuilder();
        int column = 0;
        
        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '"') {
                quoted = !quoted;
            } else if (character == ',' && !quoted) {
                if (column < columns.length) columns[column++] = buffer.toString().trim();
                buffer = new StringBuilder();
            } else {
                buffer.append(character);
            }
        }
        if (column < columns.length) columns[column] = buffer.toString().trim();
        return columns; // columns is the buffer variable used to store the data from this method. See its definition below the public class header. 
    }
}
