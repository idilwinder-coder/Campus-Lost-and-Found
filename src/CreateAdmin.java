import java.util.Scanner;

public class CreateAdmin {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        try {

            System.out.print("Admin Name: ");
            String name = sc.nextLine();

            System.out.print("Staff ID: ");
            String staffId = sc.nextLine();

            System.out.print("Admin Email: ");
            String email = sc.nextLine();

            System.out.print("Phone Number: ");
            String phone = sc.nextLine();

            System.out.print("Admin Password: ");
            String password = sc.nextLine();

            boolean result = Database.registerAdmin(
                name, staffId, email, phone, password
            );

            if (result) {
                System.out.println("Admin Account Created!");
            } else {
                System.out.println(
                    "Admin account already exists or details are duplicate."
                );
            }

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());
        }

        sc.close();
    }
}