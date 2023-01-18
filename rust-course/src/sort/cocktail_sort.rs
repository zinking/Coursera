
pub fn cocktail_sort<T: Ord>(arr: &mut [T]) {
    let len = arr.len();
    if len < 2 {
        return;
    }

    loop {
        let mut swapped = false;
        for i in 0..(len-1) {
            if arr[i] > arr[i + 1] {
                arr.swap(i, i + 1);
                swapped = true;
            }
        }
        if !swapped {
            break;
        }

        swapped = false;

        for i in (0..(len-1)).rev() {
            if arr[i] > arr[i + 1] {
                arr.swap(i, i + 1);
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
    // use crate::sort::cocktail_sort::cocktail_sort;
    use super::*;

    #[test]
    fn basic() {
        let mut arr = vec![5, 2, 1, 3, 4, 6];
        cocktail_sort(&mut arr);
        assert_eq!(arr, vec![1, 2, 3, 4, 5, 6]);
    }

    #[test]
    fn empty() {
        let mut arr = Vec::<i32>::new();
        cocktail_sort(&mut arr);
        assert_eq!(arr, vec![]);
    }

    #[test]
    fn one_element() {
        let mut arr = vec![3];
        cocktail_sort(&mut arr);
        assert_eq!( arr, vec![3]);
    }

    #[test]
    fn pre_sorted() {
        let mut arr = vec![1, 2, 3, 4, 5, 6, 7, 8, 9];
        cocktail_sort(&mut arr);
        assert_eq!(arr, vec![1, 2, 3, 4, 5, 6,  7, 8, 9]);
    }
}