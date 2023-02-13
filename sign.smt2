(goal
  (or k!15 (not k!7)) ; x negative, y positive is not allowed (*)
  (let ((a!1 (or k!1 (not (or k!8 (not k!0))))) ; a1: x[0] > y[0] or x[1] = 1
        (a!2 (or (not k!9) (not (or k!8 (not k!0)))))) ; a2: y[1] = 0 or x[0] > y[0]
  (let ((a!3 (or (not (or k!1 (not k!9))) (not a!1) (not a!2)))) ; a3: x[1] < y[1] or not a1 or not a2
  (let ((a!4 (or (not (or k!2 (not k!10)))
                 (not (or k!2 (not a!3)))
                 (not (or (not k!10) (not a!3)))))) ; a4: x[2] < y[2] or (x2=0 and a3) or (y[2]=1 and a3)
  (let ((a!5 (or (not (or k!3 (not k!11)))
                 (not (or k!3 (not a!4)))
                 (not (or (not k!11) (not a!4)))))) ; a5: x[3] < y[3] or (x3=0 and a4) or (y[3]=1 and a4)
  (let ((a!6 (or (not (or k!4 (not k!12)))
                 (not (or k!4 (not a!5)))
                 (not (or (not k!12) (not a!5))))))
  (let ((a!7 (or (not (or k!5 (not k!13)))
                 (not (or k!5 (not a!6)))
                 (not (or (not k!13) (not a!6))))))
  (let ((a!8 (or (not (or k!6 (not k!14)))
                 (not (or k!6 (not a!7)))
                 (not (or (not k!14) (not a!7)))))) ; a8: x[6:0] <= y[6:0]
    (or k!15 (not a!8)))))))))  ; y is negative or x[6:0] > y[6:0]; given (*), x and y are both positive, so x >_s y
  (let ((a!1 (or k!1 (not (or k!8 (not k!0)))))
        (a!2 (or (not k!9) (not (or k!8 (not k!0))))))
  (let ((a!3 (or (not (or k!1 (not k!9))) (not a!1) (not a!2))))
  (let ((a!4 (or (not (or k!2 (not k!10)))
                 (not (or k!2 (not a!3)))
                 (not (or (not k!10) (not a!3))))))
  (let ((a!5 (or (not (or k!3 (not k!11)))
                 (not (or k!3 (not a!4)))
                 (not (or (not k!11) (not a!4))))))
  (let ((a!6 (or (not (or k!4 (not k!12)))
                 (not (or k!4 (not a!5)))
                 (not (or (not k!12) (not a!5))))))
  (let ((a!7 (or (not (or k!5 (not k!13)))
                 (not (or k!5 (not a!6)))
                 (not (or (not k!13) (not a!6))))))
  (let ((a!8 (or (not (or k!6 (not k!14)))
                 (not (or k!6 (not a!7)))
                 (not (or (not k!14) (not a!7)))))) ; these are the same; a8: x[6:0] <= y[6:0]
    (or (not k!7) (not a!8)))))))))) ; x is positive or x[6:0] > y[6:0]; 