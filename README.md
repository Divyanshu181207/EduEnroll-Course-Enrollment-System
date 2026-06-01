# EduEnroll – Course Enrollment System

A desktop Java Swing application for managing course enrollments, learners, and enrollment records with a modern dark-themed UI.

---

## Features

- **Course Management** – Add and view courses with instructor, category, seat capacity, and live availability tracking.
- **Learner Management** – Register learners with ID, name, and email; view their total active enrollments.
- **Enrollment** – Enroll any learner into any available course with duplicate-check and full-seat validation.
- **Records & Drop** – View all enrollment records with status indicators (Active / Dropped); drop enrollments with confirmation, which automatically frees the seat.
- **Seeded Demo Data** – Launches with 4 sample courses and 3 sample learners so the UI is ready to explore immediately.

---

## Project Structure

```
CourseEnrollmentSystem.java
│
├── Person            (abstract base class — id, name, email, getRole())
├── Learner           (extends Person — tracks enrolled course IDs)
├── Course            (id, title, instructor, maxSeats, enrolled count)
├── Enrollment        (links a Learner + Course; auto-generates ENR-xxx ID)
└── CourseEnrollmentSystem   (JFrame — builds UI, wires all logic)
```

### Class Responsibilities

| Class | Responsibility |
|---|---|
| `Person` | Abstract base; holds common identity fields |
| `Learner` | Represents a student; maintains list of enrolled course IDs |
| `Course` | Holds course metadata and seat tracking (`isFull()`, `available()`) |
| `Enrollment` | Immutable record linking a learner to a course with date and status |
| `CourseEnrollmentSystem` | Main `JFrame`; builds 4 tabs and manages all state lists |

---

## UI Tabs

| Tab | Purpose |
|---|---|
| **Courses** | Add new courses via a form; view all courses in a table |
| **Learners** | Register new learners; view all learners and their enrollment count |
| **Enroll** | Select a learner and course from dropdowns; click *Enroll Now* |
| **Records** | View all enrollment records; select a row and click *Drop* to cancel |

---

## Requirements

- **Java 8 or higher** (uses `javax.swing`, `java.awt`, `java.util`)
- No external libraries or build tools required — single-file project

---

## How to Run

```bash
# Compile
javac CourseEnrollmentSystem.java

# Run
java CourseEnrollmentSystem
```

The application window (1000×650) will open centered on screen.

---

## Validation & Business Rules

- Course ID and Title are required when adding a course; duplicate IDs are rejected.
- Learner ID and Name are required; duplicate IDs are rejected.
- A learner cannot be enrolled in the same course twice (duplicate active enrollment check).
- Enrollment is blocked when a course has no free seats.
- Dropping an enrollment decrements the course seat count and removes the course from the learner's list.

---

## Seeded Demo Data

| Courses | Instructor | Seats | Category |
|---|---|---|---|
| C001 – Java Programming | Dr. Priya Sharma | 30 | Programming |
| C002 – Data Structures | Prof. Rahul Verma | 25 | CS |
| C003 – Web Development | Ms. Ananya Singh | 40 | Web |
| C004 – Machine Learning | Dr. Karan Mehta | 20 | AI/ML |

| Learners | Email |
|---|---|
| L001 – Aarav Patel | aarav@email.com |
| L002 – Diya Nair | diya@email.com |
| L003 – Rohan Gupta | rohan@email.com |

Two enrollments are pre-created: Aarav → Java Programming, Diya → Data Structures.

---

## Potential Improvements

- Persist data to a file or database (currently all data is in-memory and lost on exit).
- Add search/filter functionality to the course and learner tables.
- Support editing and deleting courses or learners.
- Add an admin/instructor role using the existing `Person` abstraction.
- Export enrollment records to CSV or PDF.
