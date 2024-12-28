-- gymfacilities
INSERT INTO gymfacilities (FacilityID, FacilityName, LAST_MAINTENANCE, Complimentary) VALUES
(1, 'Main Gym', '2024-01-15', 'Yes'),
(2, 'East Side Gym', '2024-02-10', 'No'),
(3, 'North Branch Gym', '2024-03-12', 'Yes');

-- maintenance_descriptions
INSERT INTO maintenance_descriptions (DescriptionID, FacilityID, Description) VALUES
(1, 1, 'Treadmill Repair'),
(2, 2, 'Pool Cleaning'),
(3, 3, 'AC Maintenance');

-- maintenance_records
INSERT INTO maintenance_records (FacilityID, MaintenanceDate, DescriptionID) VALUES
(1, '2024-01-16', 1),
(2, '2024-02-12', 2),
(3, '2024-03-15', 3);

-- facility_prices
INSERT INTO facility_prices (FacilityID, MonthlyPriceWithoutTrainer, MonthlyPriceWithTrainer, QuarterlyPriceWithoutTrainer, QuarterlyPriceWithTrainer, YearlyPriceWithoutTrainer, YearlyPriceWithTrainer) VALUES
(1, 50.00, 75.00, 140.00, 200.00, 500.00, 700.00),
(2, 45.00, 70.00, 130.00, 190.00, 480.00, 650.00),
(3, 55.00, 80.00, 150.00, 220.00, 520.00, 750.00);

-- users
INSERT INTO users (userID, username, password, salt, role, locked) VALUES
(1, 'johndoe', 'A3AFD5B54EE712C38A73546704A1C76BC09F5782F768BE7149E7498FEA034816', '3435FC1E428B42E80A8906E970EBEFE96C759DA6', 'receptionist', 'FALSE'),
(2, 'janedoe', 'D1CC210B462B2FDB90484D0EF5F5009369A473E6CE712F5A11E32E1395563892', '8858DF1DFBE536584E687CD74EE0138154678A89', 'trainer', 'FALSE'),
(3, 'mikeT', '5E660CB9D8A4ECA6367DB626C7ACEA639181ADF6AD49E5303B4287081D8A3088', 'AAD9F846352BF840B0AEA6776B56E109DB13F0A2', 'trainer', 'FALSE'),
(4, 'jackdoe', '528E92B5E1D0F406FACAF313977D14245ADDC8F7EE00828E9094AB67FC66B854', 'E6AA424D0FC22E512DAF9A7270D56914784BFF2F', 'manager', 'FALSE');

-- logging
INSERT INTO logging (event_date, logger, Message, username) VALUES
('2024-12-27 10:00:00', 'System', 'User logged in', 'johndoe'),
('2024-12-27 10:05:00', 'System', 'User logged out', 'janedoe'),
('2024-12-27 11:00:00', 'System', 'User logged in', 'jackdoe');

-- trainers
INSERT INTO trainers (TrainerID, Name, username, Specialization, FacilityID) VALUES
(1, 'Jane', 'janedoe', 'Yoga', 1),
(2, 'Mike', 'mikeT', 'Strength Training', 2);

-- members
INSERT INTO members (MemberID, Name, ContactInformation, MembershipType, RegistrationDate, membership_expiry) VALUES
(1, 'Alice', 'alice@example.com', 'Premium', '2024-01-01', '2025-01-01'),
(2, 'Bob', 'bob@example.com', 'Basic', '2024-02-01', '2025-02-01');

-- billing
INSERT INTO billing (MemberID, BillingDate, Amount, PaymentMethod, AdditionalInfo, FacilityID) VALUES
(1, '2024-01-01', 500.00, 'Card', 'Paid in full', 1),
(2, '2024-02-01', 200.00, 'Card', 'Monthly payment', 2);

-- classes
INSERT INTO classes (ClassID, ClassName, Weekday, Schedule, TrainerID, MaximumCapacity, CurrentEnrollment, FacilityID) VALUES
(1, 'Yoga Basics', 'Monday', '10:00:00', 1, 20, 15, 1),
(2, 'Strength Training', 'Wednesday', '08:00:00', 2, 15, 10, 2);

-- member_plan
INSERT INTO member_plan (member_id, class_id, facilityID) VALUES
(1, 1, 1),
(2, 2, 2);

-- progress_tracking
INSERT INTO progress_tracking (MemberID, TrainerID, ClassID, ProgressDate, ProgressDetails, ProgressMetric) VALUES
(1, 1, 1, '2024-02-01', 'Improved flexibility', 85.50),
(2, 2, 2, '2024-02-15', 'Increased strength', 90.00);