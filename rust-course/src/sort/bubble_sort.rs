
pub fn bubble_sort<T: PartialOrd>(arr: &mut [T]) {
    let sz = arr.len();
    if sz < 1 {
        return
    }

    for i in (0..sz).rev() {
        let mut swapped: bool = false;
        for j in 0..i {
            if arr[j] > arr[j + 1] {
                arr.swap(j + 1, j);
                swapped = true;
            }
        }

        if !swapped {
            break;
        }
    }
}

pub fn is_sorted<T: PartialOrd>(arr: &[T]) -> bool {
    let len = arr.len();
    if len < 2 {
        return true;
    }

    for i in 0..(len-1) {
        if arr[i] > arr[i + 1] {
            return false;
        }
    }
    true
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_is_sorted() {
        let empty_vect: Vec<usize> = vec![];
        let rr0 = is_sorted(&empty_vect);
        assert_eq!(true, rr0);

        let number_vec: Vec<usize> = vec![1, 3, 5];
        let rr1 = is_sorted(&number_vec);
        assert_eq!(true, rr1);

        let number_vec1: Vec<usize> = vec![1, 5, 3, 0, 2];
        let rr2 = is_sorted(&number_vec1);
        assert_eq!(false, rr2)
    }

    #[test]
    fn test_empty_vec() {
        let mut empty_vec: Vec<String> = vec![];
        bubble_sort(&mut empty_vec);
        assert_eq!(empty_vec, Vec::<String>:: new());
    }

    #[test]
    fn test_number_vec() {
        let mut number_vec = vec![3, 7, 5, 1, 9];
        bubble_sort(&mut number_vec);
        assert_eq!(number_vec, vec![1, 3, 5, 7, 9])
    }

    #[test]
    fn test_string_vec() {
        let mut string_vec = vec![
            String::from("Bob"),
            String::from("David"),
            String::from("Carol"),
            String::from("Alice")
        ];
        bubble_sort(&mut string_vec);
        assert_eq!(string_vec, vec![
            String::from("Alice"),
            String::from("Bob"),
            String::from("Carol"),
            String::from("David")
        ]);
    }
}