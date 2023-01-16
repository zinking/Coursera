
pub fn bubble_sort<T: PartialOrd>(arr: &mut [T]) {
    let sz = arr.len();
    if sz < 1 {
        return
    }

    for i in 0..(sz - 1) {
        let mut swapped: bool = false;
        for j in 1..(sz - i) {
            if arr[j - 1] > arr[j] {
                arr.swap(j - 1, j);
                swapped = true;
            }
        }

        if !swapped {
            break;
        }
    }
}

#[cfg(test)]
mod tests {
    use super::*;

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